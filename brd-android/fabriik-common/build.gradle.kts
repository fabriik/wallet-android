import brd.BrdRelease

plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

apply(from = rootProject.file("gradle/flavors.gradle"))

val DEFAULT_FABRIIK_CLIENT_TOKEN: String by project

android {
    compileSdkVersion(BrdRelease.ANDROID_COMPILE_SDK)
    buildToolsVersion(BrdRelease.ANDROID_BUILD_TOOLS)
    defaultConfig {
        minSdkVersion(BrdRelease.ANDROID_MINIMUM_SDK)
        buildConfigField("int", "VERSION_CODE", "${BrdRelease.versionCode}")
        buildConfigField("String", "DEFAULT_FABRIIK_CLIENT_TOKEN", DEFAULT_FABRIIK_CLIENT_TOKEN)
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
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":brd-android:theme"))

    api(brd.Libs.Androidx.AppCompat)
    api(brd.Libs.Androidx.CoreKtx)
    api(brd.Libs.Androidx.LifecycleLiveDataKtx)
    api(brd.Libs.Androidx.LifecycleViewModelKtx)
    api(brd.Libs.Androidx.NavigationUI)
    api(brd.Libs.Androidx.NavigationFragment)
    api(brd.Libs.Material.Core)
    api(brd.Libs.Coroutines.Core)

    api(brd.Libs.Networking.Retrofit)
    api(brd.Libs.Networking.RetrofitMoshiConverter)

    api(brd.Libs.Networking.Moshi)
    kapt(brd.Libs.Networking.MoshiCodegen)

    testImplementation(brd.Libs.JUnit.Core)
    testImplementation(brd.Libs.Mockito.Core)
    testImplementation(brd.Libs.Robolectric.Core)
}