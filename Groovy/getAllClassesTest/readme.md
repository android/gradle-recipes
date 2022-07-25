# Scoped Artifacts toGet example in Groovy
This sample shows how to obtain all the classes that will be used to create the dex files.
There are two lists that need to be used to obtain the complete set of classes because some
classes are present as .class files in directories and others are present in jar files.
Therefore, you must process both [ListProperty] of [Directory] and [RegularFile] to get the full
list.

The [onVariants] block will wire the [GetAllClassesTask] input properties (allJars and allDirectories)
by using the [ScopedArtifactsOperation.toGet] method with the right [ScopedArtifact].
`
    variant.artifacts.forScope(ScopedArtifacts.Scope.PROJECT)
        .use(taskProvider)
        .toGet(
            ScopedArtifact.CLASSES.INSTANCE,
            { it.allJars },
            { it.allDirectories }
        )
`
## To Run
./gradlew debugGetAllClasses
expected result : a list of classes and jar files.