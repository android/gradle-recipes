# toListenTo recipe for Artifact.Multiple

This sample shows how to react to a change to an artifact of type [`Artifact.Multiple`](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/Artifact.Multiple) that causes the artifact
to be regenerated. The API will make sure that the task is called when the task regenerating the artifact runs. This
allows tasks that must process an artifact to run without changing what task should be called by the user. In this
particular example, the task makes a copy of the multi-dex proguard files while renaming them.

Custom plugin is defined in [CustomPlugin.kt](build-logic/plugins/src/main/kotlin/CustomPlugin.kt).
It registers task [CopyProguardTask.kt](build-logic/plugins/src/main/kotlin/CopyProguardTask.kt) that simply copies
a proguard file somewhere else and renames it.

The API used in this recipe is [`MultipleArtifactTypeOutOperationRequest.toListenTo()`](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/MultipleArtifactTypeOutOperationRequest#toListenTo(com.android.build.api.artifact.Artifact.Multiple)),
and this is wired up to the task with [`TaskBasedOperation.wiredWithMultiple()`](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/TaskBasedOperation#wiredWithMultiple(kotlin.Function1)).
Below is an example usage of the APIs:

```
variant.artifacts.use(copyTask).wiredWithMultiple {
    it.proguardFiles
}.toListenTo(MultipleArtifact.MULTIDEX_KEEP_PROGUARD)
```

## To Run
Just type `./gradlew app:assembleDebug`
You will be able to find the renamed metadata file at
`app/build/renamed_intermediates/native_debug_metadata/debug/renamed_test_file.dbg` after copying.
