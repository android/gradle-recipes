# toListenTo recipe

This sample shows how to react to an artifact creation. The API will make sure that the task is
called when the task creating the artifact runs. This allows tasks that must process an artifact
to run without changing what task should be called by the user. In this particular example, the
task makes a copy of the APKs while renaming them.


Custom plugin is defined in [CustomPlugin.kt](build-logic/plugins/src/main/kotlin/CustomPlugin.kt).
It registers task [CopyApk.kt](build-logic/plugins/src/main/kotlin/CopyApk.kt) that simply copies
the file somewhere else using each APK's own metadata information.

`SingleArtifact.APK` is an artifact of type `Artifact.ContainsMany`. This is a type of directory-based
artifact that can actually contain many artifacts. Each artifact is associated with specificy meta-data.

Reading these artifacts from the directory require usage of `BuiltArtifactsLoader`.


## To Run
Just type `./gradlew app:assembleDebug`
You will be able to find the renamed APKs at `app/build/outputs/renamedintermediates/apk/debug/packageDebug/app-debug.apk`
and `app/build/outputs/apk/debug/app-debug.apk` after copying.
