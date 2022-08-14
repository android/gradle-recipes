plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

apply<CustomPlugin>()

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
    }
}
