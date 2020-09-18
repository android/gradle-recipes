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

    onVariantProperties {
        addBuildConfigField("FloatValue", "\"1f\"", "Float Value")
        addBuildConfigField("LongValue", "\"1L\"", "Long Value")
        addBuildConfigField("VariantName", "\"$name\"", "Variant Name")
    }
}