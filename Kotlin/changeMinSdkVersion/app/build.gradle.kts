        plugins {
                id("com.android.application")
                kotlin("android")
                kotlin("android.extensions")
        }

        import com.android.build.api.variant.AndroidVersion

        android {
            
compileSdkVersion(29)
defaultConfig {
    minSdkVersion(21)
    targetSdkVersion(29)
}
        }
        androidComponents {
            beforeVariants(selector().withName("release")) { variantBuilder ->
                variantBuilder.minSdk = 23
            }
        }