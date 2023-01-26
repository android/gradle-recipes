plugins {
        id("com.android.application")
        kotlin("android")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
    }
}

androidComponents {
    registerSourceType("toml")
}