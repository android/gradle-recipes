plugins {
        id("com.android.application")
        kotlin("android")
        kotlin("android.extensions")
}
android {
    
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
    }
}
androidComponents {
    onVariants {
        it.addBuildConfigField("FloatValue", "\"1f\"", "Float Value")
        it.addBuildConfigField("LongValue", "\"1L\"", "Long Value")
        it.addBuildConfigField("VariantName", "\"${name}\"", "Variant Name")
    }
}