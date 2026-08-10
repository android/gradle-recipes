# Register tasks to run before build using LifecycleTasks.registerPreBuild()

This sample demonstrates how to use the [`LifecycleTasks.registerPreBuild()`](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/variant/LifecycleTasks#registerPreBuild(kotlin.Array)) API
to register tasks to run before a build begins.

| Module                     | Content                                                     |
|----------------------------|-------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe. |
| [app](app)                 | An Android application that has the plugin applied.         |

The [build-logic](build-logic) sub-project contains the [`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt)
and [`PreBuildValidationTask`](build-logic/plugins/src/main/kotlin/PreBuildValidationTask.kt) classes.

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) contains the creation of the task provider for the [`PreBuildValidationTask`](build-logic/plugins/src/main/kotlin/PreBuildValidationTask.kt). The API call
to register this task per-variant is below:

```
androidComponents.onVariants { variant ->
    variant.lifecycleTasks.registerPreBuild(preBuildTaskProvider)
}
```

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) also registers the `ValidateTask` which verifies that the pre-build task ran.

## To Run
To run the example, you can just do

```
./gradlew :app:build
```

and you should see the file created by the task in `app/build/preBuildOutput/gradle_version.txt`.
