# Consuming a single artifact

This recipe shows how to add a task per variant to get and check a
[SingleArtifact](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/SingleArtifact).
This recipe uses `SingleArtifact.BUNDLE` as an example, but the code is similar for other
`SingleArtifact` types.

This recipe contains the following directories :

| Module                     | Content                                                     |
|----------------------------|-------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe. |
| [app](app)                 | An Android application that has the plugin applied.         |


The [build-logic](build-logic) sub-project contains the
[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) and
[`CheckBundleTask`](build-logic/plugins/src/main/kotlin/CheckBundleTask.kt) classes.

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) registers an instance of the
`CheckBundleTask` per variant and sets its input directory via
`variant.artifacts.get(SingleArtifact.BUNDLE)`, which automatically adds a dependency on the task
that produces `SingleArtifact.BUNDLE`.

[`CheckBundleTask`](build-logic/plugins/src/main/kotlin/CheckBundleTask.kt) does a trivial
verification of the bundle file (checking that it has the expected file extension).

To run the recipe : `gradlew checkDebugBundle`
