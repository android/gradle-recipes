# Consuming scoped artifacts

This recipe shows how to add a task per variant to get and check a
[ScopedArtifact](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/ScopedArtifact).
This recipe uses `ScopedArtifact.CLASSES` as an example, but the code is similar for other
`ScopedArtifact` types.

This recipe contains the following directories :

| Module                     | Content                                                     |
|----------------------------|-------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe. |
| [app](app)                 | An Android application that has the plugin applied.         |


The [build-logic](build-logic) sub-project contains the
[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) and
[`CheckClassesTask`](build-logic/plugins/src/main/kotlin/CheckClassesTask.kt) classes.

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) registers an instance of the
`CheckClassesTask` per variant and sets its `CLASSES` inputs via the code below,
which automatically adds a dependency on any tasks producing `CLASSES` artifacts. When
getting the final value of a scoped artifact, a Task must provide two input fields per scope, one
for a list of jars and the other for a list of directories.

```
variant.artifacts
    .forScope(ScopedArtifacts.Scope.PROJECT)
    .use(taskProvider)
    .toGet(
        ScopedArtifact.CLASSES,
        CheckClassesTask::projectJars,
        CheckClassesTask::projectDirectories,
    )
    
variant.artifacts
    .forScope(ScopedArtifacts.Scope.ALL)
    .use(taskProvider)
    .toGet(
        ScopedArtifact.CLASSES,
        CheckClassesTask::allJars,
        CheckClassesTask::allDirectories,
    )
```

In practice, a task could consider only the `PROJECT` scope or only the `ALL` scope (though
the `PROJECT` scope is a subset of the `ALL` scope). 

[`CheckClassesTask`](build-logic/plugins/src/main/kotlin/CheckClassesTask.kt) does a trivial
verification of the classes.

To run the recipe : `gradlew checkDebugClasses`
