# Transform multiple artifacts during build

This sample demonstrates how to update the Android manifest in a meaningful way.
It obtains information from the git repository and inserts it into AndroidManifest.xml.

The plugin uses `wiredWithFiles(<input>, <output>)` to modify the Android public manifest
(`SingleArtifact.MERGED_MANIFEST`) after it has been merged by standard tasks. Plugin installs
three tasks: [GitVersionTask.kt](build-logic/plugins/src/main/kotlin/GitVersionTask.kt) to
get version and put it in file and [ManifestTransformerTask.kt](build-logic/plugins/src/main/kotlin/ManifestTransformerTask.kt)
to pick up this information and update `AndroidManifest.xml`.

The sample shows how to wire multiple tasks together so that the output of the first
task is the input of the second task, and so on. Gradle places these tasks in a task
tree to be executed in the proper order during the build.

## To Run
To run the example, you can just do

```
./gradlew :app:debugManifestUpdater
```
and you will see

```
...

> Task :app:gitVersionProvider

...

> Task :app:debugManifestUpdater
Writes to .../app/build/intermediates/merged_manifest/debug/debugManifestUpdater/AndroidManifest.xml

...

SUCCESS
```
You can then find AndroidManifest.xml and check that there is a version in android:versionCode
and it's equal to "1234" (the default value).