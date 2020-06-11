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
        addResValue("VariantName", "string", name, "Variant Name")
    }
}