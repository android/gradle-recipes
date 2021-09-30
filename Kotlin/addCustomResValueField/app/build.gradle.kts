import com.android.build.api.variant.ResValue

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
    onVariants { variant ->
        variant.resValues.put(variant.makeResValueKey("string", "VariantName"),
            ResValue(name, "Variant Name"))
    }
}