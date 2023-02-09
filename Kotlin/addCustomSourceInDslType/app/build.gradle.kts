plugins {
        id("com.android.application")
        kotlin("android")
}

android {
    namespace = "com.example.customSource"
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
    }
}

androidComponents {
    registerSourceType("toml")
}