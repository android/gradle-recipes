import com.android.build.api.variant.BuildConfigField

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
        buildConfigFields.put("FloatValue", BuildConfigField("float", "1f", "Float Value"))
        buildConfigFields.put("LongValue", BuildConfigField("long", "1L", "Long Value"))
        addBuildConfigField("VariantName", "\"$name\"", "Variant Name")
    }
}