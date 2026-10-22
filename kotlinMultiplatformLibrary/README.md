# Creating Android Kotlin Multiplatform Library plugin

This recipe uses the `com.android.kotlin.multiplatform.library` plugin, which is required to create
[Kotlin Multiplatform](https://developer.android.com/kotlin/multiplatform) library modules with an Android target.

Recipe has the following module structure:

| Module           | Content                                               |
|------------------|-------------------------------------------------------|
| [shared](shared) | The shared module of the project.                     |

In the shared module, the `commonMain` source set is defined, which contains some basic
[logic](shared/src/commonMain/kotlin/com/example/kotlinMultiplatform/Content.kt) that is implemented by the
[Android-specific code](shared/src/androidMain/kotlin/com/example/kotlinMultiplatform/AndroidContent.kt). Test
sources `commonTest` and `androidHostTest` are also included to demonstrate tests for each of the content files.

## To Build
To build the KMP library artifacts, you can run:

`./gradlew :shared:assemble`