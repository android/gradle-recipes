plugins {
    id("com.android.application")
    kotlin("android")
    id("android.recipes.per_variant_manifest_placeholder")
}

android {
    namespace = "com.example.android.recipes.per_variant_manifest_placeholder"
    compileSdk = 29
    defaultConfig {
        minSdk = 21
    }
}
