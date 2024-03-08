plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(29)
                defaultConfig {
                    minSdkVersion(21)
                }
}

androidComponents.finalizeDsl { extension ->
    extension.buildTypes.create("extra").let {
       it.isJniDebuggable = true
    }
}