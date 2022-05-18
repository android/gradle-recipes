plugins {
        id("com.android.application")
        kotlin("android")
}

apply<ExamplePlugin>()

android { namespace = "com.android.build.example.minimal"
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
    }
    
}