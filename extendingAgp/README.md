# Extending AGP DSL.

This recipe demonstrates how third party plugins can extend the android DSL block and provide their users with a well
integrated experience of using the android block to configure both AGP and the third party plugin.

This is particularly useful when third party plugins want to extend build types or product flavors or have a variant
extension.

This recipe contains the following directories:

| Module                     | Content                                                                                                                   |
|----------------------------|---------------------------------------------------------------------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe.                                                               |
| [app_kts](app_kts)         | An Android application that should how those DSL extensions can be used in a [build.gradle.kts](app_kts/build.gradle.kts) |
| [app_groovy](app_groovy) | Similar example using Groovy syntax, see [build.gradle](app_groovy/build.gradle)                                          |

## Details

### DSL Extension registration

The recipe adds a new custom Project plugin called [android.recipes.extendingAgp](build-logic/plugins/build.gradle.kts)
that is configured by [CustomPlugin.kt](build-logic/plugins/src/main/kotlin/CustomPlugin.kt).

The CustomPlugin will register the following DSL extension types:

| Extension Point | Extension Type                                                                                | Details |
|-----------------|-----------------------------------------------------------------------------------------------|---------|
| BuildType       | [BuildTypeDslExtension](build-logic/plugins/src/main/kotlin/BuildTypeDslExtension.kt)         | Extension for the buildType DSL block |
| ProductFlavor   | [ProductFlavorDslExtension](build-logic/plugins/src/main/kotlin/ProductFlavorDslExtension.kt) | Extension for the productFlavor block |
| Project | [ProjectDslExtension](build-logic/plugins/src/main/kotlin/ProjectDslExtension.kt)             | Extension for the android block |
| Variant | [VariantDslExtension](build-logic/plugins/src/main/kotlin/VariantDslExtension.kt) | Extension to the Variant API. |

### DSL Extension consumption

If an extension to Project, BuildType or ProductFlavor is registered, a VariantExtension must be provided. The Variant
extension instance should represent the merged values from all registered extensions for that particular Variant. 
Only the variant extension values should be used as Tasks inputs.
This is because the variant extension can be further customized by different plugins that use the AndroidComponentExtension.onVariants API.
Therefore, tasks should only look at values in the variant object.

In practice, this means that the build type and product flavor extensions should be combined when creating the variant extension.
All fields of the variant extension must be implemented using org.gradle.api.provider.Property or a related class. 
This will ensure that the task execution block that calls org.gradle.api.provider.Property.get() will get the final 
value, regardless of the order in which plugins are configured.

As always, never call `org.gradle.api.provider.Property.get()` during configuration phase. Always set the property instance as the Task 
input, not its contained value.

The AGP team recommends following this principle. However, in the example provided, the 
[Task](build-logic/plugins/src/main/kotlin/VerifierTask.kt) is consuming all possible extensions for educational purposes.

### DSL Extension usages

We provide two examples on how to use those extension points in the build files, see [here](app_kts/build.gradle.kts) for Kotlin
and [there](app_groovy/build.gradle) for Groovy.

### Run the example

To run the examples, you can just do
```
./gradlew fullDebugDumpAllExtensions
```
and the output should be:
```
> Task :app_groovy:fullDebugDumpAllExtensions
<---- Project Extension ----->
settingOne : project_level_setting_one
settingTwo : 1
<---- BuildType Extension ----->
buildTypeSettingOne: build_type_debug
buildTypeSettingTwo: 0
<---- Product Flavor Extension ----->
productFlavorSettingOne: product_flavor_full
productFlavorSettingTwo: 0
<---- Variant Extension ----->
variantSettingOne: variant_fullDebug
variantSettingTwo: 0

> Task :app_kts:fullDebugDumpAllExtensions
<---- Project Extension ----->
settingOne : project_level_setting_one
settingTwo : 1
<---- BuildType Extension ----->
buildTypeSettingOne: build_type_debug
buildTypeSettingTwo: 0
<---- Product Flavor Extension ----->
productFlavorSettingOne: product_flavor_full
productFlavorSettingTwo: 0
<---- Variant Extension ----->
variantSettingOne: fullDebug+build_type_debug_
product_flavor_full
variantSettingTwo: 99
```

Running with a different variant will produce a different output.