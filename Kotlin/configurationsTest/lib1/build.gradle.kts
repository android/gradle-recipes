        plugins {
                id("com.android.library")
                kotlin("android")
        }

        android {
            namespace = "com.android.build.example.lib1"
compileSdkVersion(29)
defaultConfig {
    minSdkVersion(21)
}
        }