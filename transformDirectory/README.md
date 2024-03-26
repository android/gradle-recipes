# Transform artifacts of type 'Single' and kind 'Directory' with InAndOutDirectoryOperationRequest.toTransform() API

This sample shows how to use the `InAndOutDirectoryOperationRequest.toTransform()` API on an artifact of type
[Artifact.Single<Directory>](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/Artifact.Single) and [Artifact.Transformable](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/Artifact.Transformable).This method is defined in
[InAndOutDirectoryOperationRequest](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/InAndOutDirectoryOperationRequest).

This recipe contains the following directories:

| Module                     | Content                                                     |
|----------------------------|-------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe. |
| [app](app)                 | An Android application that has the plugin applied.         |

The [build-logic](build-logic) sub-project contains the [`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt), [`TransformAssetsTask`](build-logic/plugins/src/main/kotlin/TransformAssetsTask.kt) and
[`CheckAssetsTask`](build-logic/plugins/src/main/kotlin/CheckAssetsTask.kt) classes.

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) registers an instance of `TransformAssetsTask` per variant using
`InAndOutDirectoryOperationRequest.toTransform()` via the code below. This automatically creates a dependency on this
task from any task consuming the `SingleArtifact.ASSETS` artifact.

```
variant.artifacts.use(transformDebugAssets)
    .wiredWithDirectories(
        TransformAssetsTask::inputDir,
        TransformAssetsTask::outputDir)
    .toTransform(SingleArtifact.ASSETS)
```

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) also registers an instance of the `CheckAssetsTask` per variant which verifies that the
transformed artifact and directory contains the expected data. In this recipe, running this task will also run
`TransformAssetsTask`, because of the dependency on `SingleArtifact.ASSETS`.

## To Run
To execute example you need to enter command:

`./gradlew :app:checkDebugAssets`
