import brd.BrdRelease
import brd.Libs
import brd.appetize.AppetizePlugin
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.google.firebase.appdistribution.gradle.AppDistributionExtension
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import io.gitlab.arturbosch.detekt.detekt
import org.gradle.kotlin.dsl.accessors.runtime.extensionOf

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("io.gitlab.arturbosch.detekt") version "1.0.1"
    id("dev.zacsweers.redacted")
}

plugins.apply(AppetizePlugin::class)
apply(from = rootProject.file("gradle/jacoco.gradle"))
apply(from = rootProject.file("gradle/flavors.gradle"))
apply(from = rootProject.file("gradle/google-services.gradle"))
apply(from = rootProject.file("gradle/copy-font-files.gradle"))

val BDB_CLIENT_TOKEN: String by project
val useGoogleServices: Boolean by ext

redacted {
    replacementString.set("***")
}

detekt {
    ignoreFailures = true
}

android {
    compileSdkVersion(BrdRelease.ANDROID_COMPILE_SDK)
    buildToolsVersion(BrdRelease.ANDROID_BUILD_TOOLS)
    defaultConfig {
        versionCode = BrdRelease.versionCode
        versionName = BrdRelease.versionName
        applicationId = "com.fabriik.app"
        minSdkVersion(BrdRelease.ANDROID_MINIMUM_SDK)
        targetSdkVersion(BrdRelease.ANDROID_TARGET_SDK)
        buildConfigField("int", "BUILD_VERSION", "${BrdRelease.buildVersion}")
        buildConfigField("String", "BDB_CLIENT_TOKEN", BDB_CLIENT_TOKEN)
        buildConfigField("Boolean", "USE_REMOTE_CONFIG", useGoogleServices.toString())
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArgument("clearPackageData", "true")
    }
    signingConfigs {
        create("FakeSigningConfig") {
            keyAlias = "key0"
            keyPassword = "qwerty"
            storeFile = file("FakeSigningKey")
            storePassword = "qwerty"
        }
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
        animationsDisabled = true
        // TODO: execution 'ANDROIDX_TEST_ORCHESTRATOR'
    }
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }
    lintOptions {
        lintConfig = file("lint.xml")
        isQuiet = true
        isExplainIssues = true
        isAbortOnError = false
        isIgnoreWarnings = false
        isCheckReleaseBuilds = false
        disable("MissingTranslation")
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("FakeSigningConfig")
            isDebuggable = false
            isMinifyEnabled = false
            buildConfigField("boolean", "IS_INTERNAL_BUILD", "false")
            if (useGoogleServices) {
                configure<AppDistributionExtension> {
                    releaseNotes = brd.getChangelog()
                    groups = "android-team"
                }
                (extensionOf(this, "firebaseCrashlytics") as CrashlyticsExtension).apply {
                    nativeSymbolUploadEnabled = true
                    strippedNativeLibsDir = rootProject.file("external/walletkit/WalletKitJava/corenative-android/build/intermediates/stripped_native_libs/release/out").absolutePath
                    unstrippedNativeLibsDir = rootProject.file("external/walletkit/WalletKitJava/corenative-android/build/intermediates/cmake/release/obj").absolutePath
                }
            }
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("FakeSigningConfig")
            applicationIdSuffix = ".debug"
            isDebuggable = true
            isJniDebuggable = true
            isMinifyEnabled = false
            buildConfigField("boolean", "IS_INTERNAL_BUILD", "true")
            packagingOptions {
                doNotStrip += "**/*.so"
            }
            if (useGoogleServices) {
                configure<AppDistributionExtension> {
                    releaseNotes = brd.getChangelog()
                    groups = "android-team"
                }
                (extensionOf(this, "firebaseCrashlytics") as CrashlyticsExtension).apply {
                    nativeSymbolUploadEnabled = true
                    strippedNativeLibsDir = rootProject.file("external/walletkit/WalletKitJava/corenative-android/build/intermediates/stripped_native_libs/debug/out").absolutePath
                    unstrippedNativeLibsDir = rootProject.file("external/walletkit/WalletKitJava/corenative-android/build/intermediates/cmake/debug/obj").absolutePath
                }
            }
        }
    }

    applicationVariants.all {
        outputs.filterIsInstance<BaseVariantOutputImpl>().forEach { output ->
            output.outputFileName = "${output.baseName}-${BrdRelease.internalVersionName}.apk"
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.4"
        freeCompilerArgs += listOf(
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.coroutines.FlowPreview",
            "-Xopt-in=kotlin.time.ExperimentalTime",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }
}

dependencies {
    implementation(project(":cosmos-bundled"))
    implementation(project(":brd-android:app-core"))
    implementation(project(":brd-android:kyc"))
    implementation(project(":brd-android:buy"))
    implementation(project(":brd-android:trade"))
    implementation(project(":brd-android:support"))
    implementation(project(":brd-android:registration"))
    implementation(project(":brd-android:fabriik-common"))
    implementation(project(":brd-android:checkout"))
    implementation(Libs.WalletKit.CoreAndroid)

    // AndroidX
    implementation(Libs.Androidx.Biometric)
    implementation(Libs.Androidx.LifecycleExtensions)
    implementation(Libs.Androidx.LifecycleScopeKtx)
    implementation(Libs.Androidx.WorkManagerKtx)
    implementation(Libs.Androidx.CoreKtx)
    implementation(Libs.Androidx.AppCompat)
    implementation(Libs.Androidx.CardView)
    implementation(Libs.Androidx.ConstraintLayout)
    implementation(Libs.Androidx.GridLayout)
    implementation(Libs.Androidx.RecyclerView)
    implementation(Libs.Androidx.Security)
    implementation(Libs.Androidx.LegacyV13)
    implementation(Libs.AndroidxCamera.Core)
    implementation(Libs.AndroidxCamera.Camera2)
    implementation(Libs.AndroidxCamera.Lifecycle)
    implementation(Libs.AndroidxCamera.View)
    implementation(Libs.Checkout.Frames)
    androidTestImplementation(Libs.AndroidxTest.EspressoCore)
    androidTestImplementation(Libs.AndroidxTest.Runner)
    androidTestImplementation(Libs.AndroidxTest.Rules)
    androidTestImplementation(Libs.AndroidxTest.JunitKtx)
    androidTestImplementation(Libs.Androidx.WorkManagerTesting)

    // Test infrastructure
    testImplementation(Libs.JUnit.Core)
    androidTestImplementation(Libs.Mockito.Android)
    androidTestImplementation(Libs.Kaspresso.Core)
    androidTestImplementation(Libs.Kakao.Core)

    // Google/Firebase
    implementation(Libs.Material.Core)
    implementation(Libs.Firebase.ConfigKtx)
    implementation(Libs.Firebase.Analytics)
    implementation(Libs.Firebase.Messaging)
    implementation(Libs.Firebase.Crashlytics)
    implementation(Libs.Firebase.FunctionsKtx)
    implementation(Libs.Guava.Core)
    implementation(Libs.Zxing.Core)

    // Square
    implementation(Libs.Picasso.Core)
    implementation(Libs.OkHttp.Core)
    implementation(Libs.OkHttp.LoggingInterceptor)
    androidTestImplementation(Libs.OkHttp.MockWebServer)

    // Webserver/Platform
    implementation(Libs.ApacheCommons.IO)
    implementation(Libs.Jbsdiff.Core)
    implementation(Libs.Slf4j.Api)
    implementation(Libs.Jetty.Webapp)
    implementation(Libs.Jetty.WebSocket)

    // Kotlin libraries
    implementation(Libs.Kotlin.StdLibJdk8)
    implementation(Libs.Coroutines.Core)
    implementation(Libs.Coroutines.Android)
    testImplementation(Libs.Coroutines.Test)
    testImplementation(Libs.Kotlin.Test)
    testImplementation(Libs.Kotlin.TestJunit)
    androidTestImplementation(Libs.Kotlin.TestJunit)

    // Mobius
    implementation(Libs.Mobius.Core)
    implementation(Libs.Mobius.Android)
    implementation(Libs.Mobius.Coroutines)
    testImplementation(Libs.Mobius.Test)

    // Fastadapter
    implementation(Libs.FastAdapter.Core)
    implementation(Libs.FastAdapter.DiffExtensions)
    implementation(Libs.FastAdapter.DragExtensions)
    implementation(Libs.FastAdapter.UtilExtensions)

    // Conductor
    implementation(Libs.Conductor.Core)
    implementation(Libs.Conductor.ViewPager)

    // Kodein DI
    implementation(Libs.Kodein.CoreErasedJvm)
    implementation(Libs.Kodein.FrameworkAndroidX)

    // Debugging/Monitoring
    debugImplementation(Libs.LeakCanary.Core)
    debugImplementation(Libs.AnrWatchdog.Core)

    compileOnly(Libs.Redacted.Annotation)

    detektPlugins(Libs.Detekt.Formatting)
    implementation("com.airbnb.android:lottie:4.2.0")
    implementation(Libs.Checkout.Frames)
}
