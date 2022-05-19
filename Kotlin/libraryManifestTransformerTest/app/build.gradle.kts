plugins {
        id("com.android.application")
        kotlin("android")
}
android {
        namespace = "com.android.build.example.minimal"
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
    }
}
dependencies {
    api(project(":module"))
}