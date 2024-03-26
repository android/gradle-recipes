# Consuming an instance of `MultipleArtifact`

This recipe shows how to add a task per variant to get and check an instance of
[MultipleArtifact](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/MultipleArtifact).
This recipe uses `MultipleArtifact.MULTIDEX_KEEP_PROGUARD` as an example, but the code is similar
for other `MultipleArtifact` types.

This recipe contains the following directories :

| Module                     | Content                                                     |
|----------------------------|-------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe. |
| [app](app)                 | An Android application that has the plugin applied.         |


The [build-logic](build-logic) sub-project contains the
[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) and
[`CheckMultiDexKeepProguardTask`](build-logic/plugins/src/main/kotlin/CheckMultiDexKeepProguardTask.kt)
classes.

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) registers an instance of the
`CheckMultiDexKeepProguardTask` per variant and adds
`variant.artifacts.getAll(MultipleArtifact.MULTIDEX_KEEP_PROGUARD)` to its input, which
automatically adds dependencies on any task that produces `MultipleArtifact.MULTIDEX_KEEP_PROGUARD`
artifacts.

[`CheckMultiDexKeepProguardTask`](build-logic/plugins/src/main/kotlin/CheckMultiDexKeepProguardTask.kt)
does a trivial verification of the multiDexKeepProguard files.

To run the recipe : `gradlew checkDebugMultiDexKeepProguardFiles`
