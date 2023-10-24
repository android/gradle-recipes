# Creating a single artifact

This recipe shows how to add a task per variant to create a
[SingleArtifact](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/SingleArtifact).
This recipe uses `SingleArtifact.MERGED_MANIFEST` as an example, but the code is similar for other
`SingleArtifact` types.

This recipe contains the following directories :

| Module                     | Content                                                     |
|----------------------------|-------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe. |
| [app](app)                 | An Android application that has the plugin applied.         |


The [build-logic](build-logic) sub-project contains the
[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) and
[`ProduceManifestTask`](build-logic/plugins/src/main/kotlin/ProduceManifestTask.kt) classes.

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) registers an instance of the
`ProduceManifestTask` task per variant and use 
[TaskBasedOperation::wiredWith](https://developer.android.com/reference/tools/gradle-api/7.3/com/android/build/api/artifact/TaskBasedOperation#wiredWith(kotlin.Function1))
and
[OutOperationRequest::toCreate](https://developer.android.com/reference/tools/gradle-api/7.3/com/android/build/api/artifact/OutOperationRequest#toCreate(com.android.build.api.artifact.Artifact.Single)) api
to wire this task's output to create `SingleArtifact.MERGED_MANIFEST`.
It also registers a `VerifyManifestTask` that does a simple verification that the `SingleArtifact.MERGED_MANIFEST` is replaced by the output of `ProduceManifestTask` task.

Please note that the exact usage in this recipe is just for demonstrating the [OutOperationRequest::toCreate](https://developer.android.com/reference/tools/gradle-api/7.3/com/android/build/api/artifact/OutOperationRequest#toCreate(com.android.build.api.artifact.Artifact.Single)) API.
There are better ways to control the manifest content per variant, and this API should only be used for the MERGED_MANIFEST artifact in rare cases.

To run the recipe : `gradlew :app:verifyDebugManifest`