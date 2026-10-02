# Transform artifacts of type 'Multiple' with CombiningOperationRequest.toTransform() API

This sample shows how to use the `CombiningOperationRequest.toTransform()` API on an object of type
[Artifact.Multiple](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/Artifact.Multiple) and [Artifact.Transformable](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/Artifact.Transformable). This method is defined in [CombiningOperationRequest](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/CombiningOperationRequest).

This recipe also demonstrates the usage of [`TaskBasedOperation.wiredWith()`](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/TaskBasedOperation#wiredWith(kotlin.Function1)),
which is used to set up the `CombiningOperationRequest` transformation by hooking up the task inputs and output.

This recipe contains the following directories:

| Module                     | Content                                                     |
|----------------------------|-------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe. |
| [app](app)                 | An Android application that has the plugin applied.         |

The [build-logic](build-logic) sub-project contains the [`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt), [`TransformNativeDebugMetadataTask`](build-logic/plugins/src/main/kotlin/TransformNativeDebugMetadataTask.kt) and
[`CheckNativeDebugMetadataTask`](build-logic/plugins/src/main/kotlin/CheckNativeDebugMetadataTask.kt) classes.

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) adds a file to the `MultipleArtifact.NATIVE_DEBUG_METADATA` artifact, and registers an instance
of `TransformNativeDebugMetadataTask` per variant using `CombiningOperationRequest.toTransform()`, which requires that
all task inputs be combined into a single output. This automatically creates a dependency on this task from any task
consuming the `MultipleArtifact.NATIVE_DEBUG_METADATA` artifact. Below is a sample usage of both
`TaskBasedOperation.wiredWith()` and `CombiningOperationRequest.toTransform()`:

```
variant.artifacts.use(transformDebugNativeDebugMetadata)
    .wiredWith(
        TransformNativeDebugMetadataTask::inputDirectories,
        TransformNativeDebugMetadataTask::outputDir
    ).toTransform(MultipleArtifact.NATIVE_DEBUG_METADATA)
```

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) also registers an instance of the `CheckNativeDebugMetadataTask` per variant which verifies that
the transformed artifact and directory contains the expected data. In this recipe, running this task will also run
`TransformNativeDebugMetadataTask`, because of the dependency on `MultipleArtifact.NATIVE_DEBUG_METADATA`.

## To Run
To execute example you need to enter command:

`./gradlew :app:checkDebugNativeDebugMetadata`
