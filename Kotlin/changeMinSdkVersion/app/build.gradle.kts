plugins {
        id("com.android.application")
        kotlin("android")
}

import com.android.build.api.variant.AndroidVersion

android {
    namespace = "com.android.build.example.minimal"
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
    }
}
androidComponents {
    beforeVariants(selector().withName("release")) { variantBuilder ->
        variantBuilder.minSdk = 23
    }
}