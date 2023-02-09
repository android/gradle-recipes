# artifacts.transform MultipleArtifact in Kotlin
This sample shows how to transform all the classes that will be used to create the dex files.
There are two lists that need to be used to obtain the complete set of classes because some
classes are present as .class files in directories and others are present in jar files.
Therefore, you must query both [ListProperty] of [Directory] and [RegularFile] to get the full list.

In this example, we only query the [ListProperty] of [Directory] to invoke some bytecode
instrumentation on classes.

The Variant API provides a convenient API to transform bytecodes based on ASM but this example
is using javassist to show how this can be done using a different bytecode enhancer.


The [onVariants] block will wire the [ModifyClassesTask] input properties (allClasses]
to the [output] folder
`wiredWith(ModifyClassesTask::allClasses, ModifyClassesTask::output)`
to transform [ScopedArtifact.CLASSES]

## To Run
./gradlew :app:assembleDebug
expected result : a list of classes and jar files.