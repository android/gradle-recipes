# Appending a generated directory to an instance of `MultipleArtifact`

This recipe shows how to add a generated directory to an instance of
[MultipleArtifact](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/MultipleArtifact).
This recipe uses `MultipleArtifact.NATIVE_DEBUG_METADATA` as an example, but the code is similar
for the other `MultipleArtifact` types that implement `Appendable`.

Note: for an example of adding a *static* directory to an instance of `MultipleArtifact`, see the
[addMultipleArtifact](../addMultipleArtifact) recipe.

This recipe contains the following directories :

| Module                     | Content                                                     |
|----------------------------|-------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe. |
| [app](app)                 | An Android application that has the plugin applied.         |


The [build-logic](build-logic) sub-project contains the
[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt),
[`GenerateNativeDebugMetadataTask`](build-logic/plugins/src/main/kotlin/GenerateNativeDebugMetadataTask.kt),
and [`CheckBundleTask`](build-logic/plugins/src/main/kotlin/CheckBundleTask.kt) classes.

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) registers an instance of
`GenerateNativeDebugMetadataTask` per variant and appends its output directory to the
`MultipleArtifact.NATIVE_DEBUG_METADATA` artifacts as shown below. This automatically creates a
dependency on this task from any task consuming the MultipleArtifact.NATIVE_DEBUG_METADATA
artifacts.

```
variant.artifacts.use(generateNativeDebugMetadataTask)
    .wiredWith(GenerateNativeDebugMetadataTask::output)
    .toAppendTo(MultipleArtifact.NATIVE_DEBUG_METADATA)
```

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) also registers an instance of
the `CheckBundleTask` per variant which verifies that the app bundle contains the expected native
debug metadata entries.

To run the recipe : `gradlew checkDebugBundle`
