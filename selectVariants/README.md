# Creating and selecting multiple variants in plugin

This recipe shows how to create multiple variants by configuring build types and flavours.
Plugin demonstrate multiple possible usages of the selector() API in
[CustomPlugin.kt](build-logic/plugins/src/main/kotlin/CustomPlugin.kt).


| Module                     | Content                                                                  |
|----------------------------|--------------------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe.              |
| [app](app)                 | An Android application that will be configured with additional variants. |


Understanding variants and selector API is important as it's widely used for creating
specific versions of your application. It can be "demo" version or some version for mobile
devices vs TV.

## Details
### Configuring variants
Variants are set with two predefined build types `debug` and `release``.
Additional variants are configured with `productFlavors` in [build.gradle](app/build.gradle.kts)

### Selecting variants.
Selecting variants is usually done to change variant's specific settings or registering tasks for particular variant.

Recipe has multiple examples of usage selector API in Kotlin:

Following snippet selects all variants with `release` build type and executes callback that
updates shrinkResources and isMinifyEnabled for variants.

```
val releaseSelector = androidComponents.selector().withBuildType("release")

androidComponents.beforeVariants(releaseSelector) { variantBuilder ->
     variantBuilder.shrinkResources = true
     variantBuilder.isMinifyEnabled = true
}
```

`beforeVariants` allows users to register a function that will be passed a Variant Builder.
The [VariantBuilder] has multiple writable properties that will impact the project configuration
and build flows. Once all `beforeVariants` callbacks have executed, AGP will create Variant instances
for each variant. The [Variant] instances, only impacts tasks' execution but cannot change the build flow 
any longer (for instance, you cannot change if minification is turned on with [Variant] instances, you must 
use the [VariantBuilder] instances).

More examples are in [CustomPlugin.kt](build-logic/plugins/src/main/kotlin/CustomPlugin.kt).

## To Run
To run the examples, you can just do

```
./gradlew assemble
```

Multiple variants will be build `fullMinApi24Release`, `demoMinApi24Release`, `fullMinApi21Debug` etc.
with different settings.
