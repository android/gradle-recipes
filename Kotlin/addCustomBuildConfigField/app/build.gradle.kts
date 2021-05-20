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
        }

        androidComponents {
            onVariants {
                it.buildConfigFields.put("FloatValue", BuildConfigField("Float", "1f", "Float Value" ))
                it.buildConfigFields.put("LongValue", BuildConfigField("Long", "1L", "Long Value" ))
                it.buildConfigFields.put("VariantName", BuildConfigField("String", "\", $, {name}\"", "Variant Name" ))
            }
        }