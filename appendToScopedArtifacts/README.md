# Appending to scoped artifacts

This recipe shows how to add a task per variant to add a directory or file to [ScopedArtifact](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/ScopedArtifact).
This recipe uses `ScopedArtifact.CLASSES` as an example, but the code is similar for other
`ScopedArtifact` types. The API used is `toAppend()` which is defined in [ScopedArtifactsOperation](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/variant/ScopedArtifactsOperation)

This recipe contains the following directories :

| Module                     | Content                                                     |
|----------------------------|-------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe. |
| [app](app)                 | An Android application that has the plugin applied.         |

The [build-logic](build-logic) sub-project contains the [`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) and [`CheckClassesTask`](build-logic/plugins/src/main/kotlin/CheckClassesTask.kt) classes.

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) registers instances of two append tasks (`AddDirectoryClassTask` and `AddJarClassTask`) per
variant and sets its `CLASSES` inputs via the code below, which automatically adds a dependency on any tasks producing
`CLASSES` artifacts. It specifies the scope for each task using `forScope()`. Additionally, the
[`CheckClassesTask`](build-logic/plugins/src/main/kotlin/CheckClassesTask.kt) is registered per variant to validate the classes have been added to the `ScopedArtifact`.

Below is a snippet that demonstrates the usage of the `toAppend()` API, for different scopes and both a directory and
file:

```
variant.artifacts
    .forScope(ScopedArtifacts.Scope.PROJECT)
    .use(addDirectoryClassTaskProvider)
    .toAppend(
        ScopedArtifact.CLASSES,
        AddDirectoryClassTask::outputDirectory
    )

variant.artifacts
    .forScope(ScopedArtifacts.Scope.ALL)
    .use(addJarClassTaskProvider)
    .toAppend(
        ScopedArtifact.CLASSES,
        AddJarClassTask::outputJar
    )
```

In practice, a task could consider only the `PROJECT` scope or only the `ALL` scope (though
the `PROJECT` scope is a subset of the `ALL` scope).

[`CheckClassesTask`](build-logic/plugins/src/main/kotlin/CheckClassesTask.kt) does a trivial verification of the classes.

To run the recipe : `gradlew checkDebugClasses`
