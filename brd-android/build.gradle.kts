buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        jcenter()
    }

    dependencies {
        classpath(brd.Libs.Redacted.Plugin)
        classpath(brd.Libs.Android.GradlePlugin)
        classpath(brd.Libs.Google.ServicesPlugin)
        classpath(brd.Libs.Android.SafeArgsPlugin)
        classpath(brd.Libs.Kotlin.GradlePlugin)
        classpath(brd.Libs.Firebase.DistributionPlugin)
        classpath(brd.Libs.Firebase.CrashlyticsPlugin)
        classpath(brd.Libs.Tests.JacocoPlugin)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
        maven(url = "https://www.jitpack.io")
    }
}
