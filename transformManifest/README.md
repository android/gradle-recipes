# Transform artifacts with kind `File` during build

This sample demonstrates how to transform the Android manifest in a meaningful way.
It obtains information from the git repository and inserts it into AndroidManifest.xml.

The plugin uses `wiredWithFiles(<input>, <output>)` to modify the Android public manifest
(`SingleArtifact.MERGED_MANIFEST`) after it has been merged by standard tasks. The transformation is done with the
[InAndOutFileOperationRequest](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/InAndOutFileOperationRequest).toTransform() API which is used for objects of type [Artifact.Single](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/Artifact.Single) and
[Artifact.Transformable](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/Artifact.Transformable).
The plugin registers three tasks: [GitVersionTask.kt](build-logic/plugins/src/main/kotlin/GitVersionTask.kt) which models a task that gets the Git version details of the
project, [ManifestTransformerTask.kt](build-logic/plugins/src/main/kotlin/ManifestTransformerTask.kt) which puts the git version in the Android manifest, and
[CheckMergedManifestTask.kt](build-logic/plugins/src/main/kotlin/CheckMergedManifestTask.kt) to validate the artifact contains the transformed data.

The sample shows how to wire multiple tasks together so that the output of the first
task is the input of the second task, and so on. Gradle places these tasks in a task
tree to be executed in the proper order during the build.

## To Run
To run the example, you can just do

```
./gradlew :app:checkDebugMergedManifest
```
