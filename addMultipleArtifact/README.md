# Adding a static directory to an instance of `MultipleArtifact`

This recipe shows how to add a static directory to an instance of
[MultipleArtifact](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/MultipleArtifact).
This recipe uses `MultipleArtifact.NATIVE_DEBUG_METADATA` as an example, but the code is similar
for other `MultipleArtifact` types.

Note: for an example of adding a *generated* directory to an instance of `MultipleArtifact`, see
the [appendToMultipleArtifact](../appendToMultipleArtifact) recipe.

This recipe contains the following directories :

| Module                     | Content                                                     |
|----------------------------|-------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe. |
| [app](app)                 | An Android application that has the plugin applied.         |


The [build-logic](build-logic) sub-project contains the
[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) and
[`CheckBundleTask`](build-logic/plugins/src/main/kotlin/CheckBundleTask.kt) classes.

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) adds the
`native-debug-metadata/extra` directory to the `MultipleArtifact.NATIVE_DEBUG_METADATA` artifacts
like so:

```
variant.artifacts
    .add(
        MultipleArtifact.NATIVE_DEBUG_METADATA,
        project.layout.projectDirectory.dir("native-debug-metadata/extra")
    )
```

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) also registers an instance of
the `CheckBundleTask` per variant and sets its input directory via
`variant.artifacts.get(SingleArtifact.BUNDLE)`, which automatically adds a dependency on the task
that produces `SingleArtifact.BUNDLE`.

[`CheckBundleTask`](build-logic/plugins/src/main/kotlin/CheckBundleTask.kt) verifies that the app
bundle contains the expected native debug metadata entries.

To run the recipe : `gradlew checkDebugBundle`
