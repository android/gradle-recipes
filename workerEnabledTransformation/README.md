# Worker Enable Transformation recipe

This sample shows how to transform the artifact that has artifact directory type.
It copies the build APK to the specified directory.

Custom plugin is defined in [CustomPlugin.kt](build-logic/plugins/src/main/kotlin/CustomPlugin.kt).
It registers task [CopyApksTask.kt](build-logic/plugins/src/main/kotlin/CopyApksTask.kt) that creates asynchronous
`WorkAction` that copies APK file.

WorkAction is part of Gradle [Worker API](https://docs.gradle.org/current/userguide/worker_api.html)
- a standard way of developing parallel tasks.
It enables the division of a task action into smaller, independent units of work.
These units can then be executed simultaneously and independently, maximizing resource utilization
and accelerating build completion. Android Gradle Plugin made it simpler to use Gradle workers API with
[ArtifactTransformationRequest](https://developer.android.com/reference/tools/gradle-api/8.2/com/android/build/api/artifact/ArtifactTransformationRequest).

## To Run
Just type `./gradlew copyDebugApks`
you will be able to find two APKs: before copying its
`app/build/intermediates/apk/debug/packageDebug/app-debug.apk`
and `app/build/outputs/apk/debug/app-debug.apk` after copying.
