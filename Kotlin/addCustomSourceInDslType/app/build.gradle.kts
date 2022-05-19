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
    namespace="com.example.customsourceindsl"
}

androidComponents {
    registerSourceType("toml")
}