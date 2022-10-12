import brd.BrdRelease
import brd.Libs

plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    id("dev.zacsweers.redacted")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

apply(from = rootProject.file("gradle/flavors.gradle"))

redacted {
    replacementString.set("***")
}

android {
    compileSdkVersion(BrdRelease.ANDROID_COMPILE_SDK)
    buildToolsVersion(BrdRelease.ANDROID_BUILD_TOOLS)
    defaultConfig {
        minSdkVersion(BrdRelease.ANDROID_MINIMUM_SDK)
        buildConfigField("int", "VERSION_CODE", "${BrdRelease.versionCode}")
    }
    lintOptions {
        isAbortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":brd-android:registration"))
    implementation(project(":brd-android:app-core"))
    implementation(project(":brd-android:fabriik-common"))
    implementation(project(":brd-android:trade"))
    implementation(project(":brd-android:kyc"))

    implementation(Libs.Androidx.AppCompat)
    implementation(Libs.Androidx.CoreKtx)
    implementation(Libs.Androidx.LifecycleLiveDataKtx)
    implementation(Libs.Androidx.LifecycleViewModelKtx)

    implementation(Libs.Checkout.Frames)

    implementation(Libs.Material.Core)

    implementation(Libs.Networking.Retrofit)
    implementation(Libs.Networking.RetrofitMoshiConverter)

    implementation(Libs.Networking.Moshi)
    kapt(Libs.Networking.MoshiCodegen)

    testImplementation(Libs.JUnit.Core)
    testImplementation(Libs.Mockito.Core)
    testImplementation(Libs.Robolectric.Core)
}