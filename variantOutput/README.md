# Getting and updating variant output using the Variant API

This recipe shows how get a variant output object and update its properties using the Variant API.

| Module                     | Content                                                                  |
|----------------------------|--------------------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe.              |
| [app](app)                 | An Android application that will be configured with additional variants. |

This recipe registers two product flavors (`flavor1` and `flavor2`), creating four different variants: `flavor1Debug`,
`flavor1Release`, `flavor2Debug`, and `flavor2Release`. Then, in [CustomPlugin.kt](build-logic/plugins/src/main/kotlin/CustomPlugin.kt),
some of these variants are used to get their output objects. The properties of the outputs are accessed and updated.
For instance, the following snippet is an example usage of updating the `versionName` of variants with a specific
flavor:

```
androidComponents.onVariants(androidComponents.selector().withFlavor("dimension1", "flavor1")) { variant ->
    val variantOutput = variant.outputs.first {
        it.outputType == VariantOutputConfiguration.OutputType.SINGLE
    }
    variantOutput.versionName.set("updatedFlavor1DebugVersionName")
}
```

Here, the variant output is of type `SINGLE`, which represents the output for the main APK.

Lastly, the [CheckMergedManifestTask.kt](build-logic/plugins/src/main/kotlin/CheckMergedManifestTask.kt) is registered. This task gets the variant's merged manifest files, and
validates that the updated properties are present.

## To Run
To run the examples, you can just do

```
./gradlew :app:checkFlavor1DebugMergedManifest
```
and
```
./gradlew :app:checkFlavor2ReleaseMergedManifest
```
