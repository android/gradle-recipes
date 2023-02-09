# artifacts.transform MultipleArtifact in Kotlin
This sample shows how to add new classes to the set that will be used to create the dex files.
There are two lists that need to be used to obtain the complete set of classes because some
classes are present as .class files in directories and others are present in jar files.
Therefore, you must query both [ListProperty] of [Directory] and [RegularFile] to get the full list.

In this example, we only add classes the [ListProperty] of [Directory].

The [onVariants] block will wire the [AddClassesTask] [output] folder using
`wiredWith(AddClassesTask::output)`
to add classes to [ScopedArtifact.CLASSES]

## To Run
./gradlew :app:assembleDebug
expected result : an APK with added types in its dex files.