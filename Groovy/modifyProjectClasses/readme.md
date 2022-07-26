# Scoped Artifacts to transform project classes in Groovy
This sample shows how to transform all the classes that will be used to create the dex files.
There are two lists that need to be used to obtain the complete set of classes because some
classes are present as .class files in directories and others are present in jar files.
Therefore, you must process both [ListProperty] of [Directory] and [RegularFile] to get the full
list.

The Variant API provides a convenient API to transform bytecodes based on ASM but this example
is using javassist to show how this can be done using a different bytecode enhancer.

The [onVariants] block will wire the [ModifyClassesTask] input properties [allJars] and
[allDirectories] to the [output] folder
`
    variant.artifacts
        .forScope(ScopedArtifacts.Scope.PROJECT)
        .use(taskProvider)
        .toTransform(
            ScopedArtifact.CLASSES.INSTANCE,
            { it.getAllJars() },
            { it.getAllDirectories() },
            { it.getOutput() })
`
to transform [MultipleArtifact.ALL_CLASSES_DIRS]

## To Run
./gradlew :app:assembleDebug
expected result : a list of classes and jar files.