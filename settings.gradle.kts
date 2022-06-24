rootProject.name = "brd-mobile"

include(
    "cosmos-core",
    "cosmos-brd-api-client",
    //"cosmos-websocket",
    "cosmos-bundled",
    "cosmos-preferences",
    "cosmos-bakers-api-client"
)

include(
    "brd-android:app",
    "brd-android:app-core",
    "brd-android:kyc",
    "brd-android:buy",
    "brd-android:trade",
    "brd-android:support",
    "brd-android:theme",
    "brd-android:registration",
    "brd-android:fabriik-common"
)

includeBuild("external/walletkit/WalletKitJava") {
    dependencySubstitution {
        substitute(module("com.breadwallet.core:corecrypto-android"))
            .with(project(":corecrypto-android"))
    }
}

includeBuild("external/redacted-compiler-plugin") {
    dependencySubstitution {
        substitute(module("dev.zacsweers.redacted:redacted-compiler-gradle-plugin"))
            .with(project(":redacted-compiler-plugin-gradle"))
        substitute(module("dev.zacsweers.redacted:redacted-compiler-plugin-annotations"))
            .with(project(":redacted-compiler-plugin-annotations"))
    }
}
