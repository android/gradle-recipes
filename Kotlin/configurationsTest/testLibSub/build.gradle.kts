        plugins {
                id("com.android.library")
                kotlin("android")
        }

        android {
            namespace = "com.android.build.example.testLibSub"
compileSdkVersion(29)
defaultConfig {
    minSdkVersion(21)
}
        }