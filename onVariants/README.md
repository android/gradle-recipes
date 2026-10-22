# Registering callbacks using onVariants in plugin

This recipe shows how to access and modify the variant objects in your project.
Plugin demonstrates a possible usages of the onVariants() API in
[CustomPlugin.kt](build-logic/plugins/src/main/kotlin/CustomPlugin.kt).


| Module                     | Content                                                                  |
|----------------------------|--------------------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe.              |
| [app](app)                 | An Android application that will be configured with additional variants. |


The onVariants API differs from beforeVariants in that it gives access to the variants only after all the artifacts
produced by AGP have been determined. This means the only values that can be modified are `Property` values, which
are resolved during task execution. One of these properties is the application ID, which is used as an example in this
recipe. These properties can then be wired up to providers from custom tasks, as this recipe demonstrates.

## Details
### Configuring variants
Variants are set with two predefined build types `debug` and `release`.

### onVariants
The onVariants API also supports `VariantSelectors`.

Recipe has multiple examples of usage onVariants API in Kotlin:

Following snippet selects all variants with `release` build type and executes callback that
updates the applicationId for those variants.

```
val releaseSelector = androidComponents.selector().withBuildType("release")

androidComponents.onVariants(releaseSelector) { variant ->
     variant.applicationId.set("newApplicationId")
}
```

More examples are in [CustomPlugin.kt](build-logic/plugins/src/main/kotlin/CustomPlugin.kt).

## To Run
[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) registers an instance of a task
`CheckApplicationIdTask` per variant, and this task verifies and outputs a modified application ID corresponding to the
variant name.

To run the examples, you can just do

```
./gradlew checkDebugApplicationId
```

and you will see the output:

```
    Application ID for debug variant: debug.applicationId
```

Running with a different variant will produce the corresponding output.