/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("android.recipes.extendingAgp")
}

android {
    namespace = "com.example.android.extendingAgp"
    compileSdk = 34
    defaultConfig {
       minSdk = 21
       targetSdk = 34
    }
    flavorDimensions += "version"
    productFlavors {
        create("demo")  {
            dimension = "version"
            applicationId = "com.example.app.demo"

            // this block will configure the "demo" ProductFlavor specific extension defined by the extendingAgp plugin.
            extensions.configure<ProductFlavorDslExtension> {
                productFlavorSettingOne = "product_flavor_demo"
                productFlavorSettingTwo = 99
            }
        }
        create("full")  {
            dimension = "version"
            applicationId = "com.example.app.full"
            // this block will configure the "full" ProductFlavor specific extension defined by the extendingAgp plugin.
            extensions.configure<ProductFlavorDslExtension> {
                productFlavorSettingOne = "product_flavor_full"
            }
        }
    }
    buildTypes {
        debug {
            // this block will configure the "debug" build type specific extension defined by the extendingAgp plugin.
            // note that the "release" build type does not have a custom extension defined.
            extensions.configure<BuildTypeDslExtension> {
                buildTypeSettingOne = "build_type_debug"
            }
        }
    }
    // this block will configure the Project level extension defined by the extendingAgp plugin.
    dslExtension {
        settingOne = "project_level_setting_one"
        settingTwo = 1
    }
}

androidComponents {
    onVariants { variant ->
        // this configures the variant specific extension point.
        variant.getExtension(VariantDslExtension::class.java)?.let {
            it.variantSettingOne.set("${variant.name}+${it.variantSettingOne.get()}")
            it.variantSettingTwo.set(99)
        }
    }
}
