# artifacts.getAll in Groovy
This sample show how to obtain all the classes that will be used to create the dex files.
There are two lists that need to be used to obtain the complete set of classes because some
classes are present as .class files in directories and others are present in jar files.
Therefore, you must query both [ListProperty] of [Directory] and [RegularFile] to get the full list.

The [onVariants] block will wire the [GetAllClassesTask] input properties (allClasses and allJarsWithClasses)
by using the [Artifacts.getAll] method with the right [MultipleArtifact].
`allClasses.set(variant.artifacts.getAll(MultipleArtifact.ALL_CLASSES_DIRS))`
## To Run
./gradlew debugGetAllClasses
expected result : a list of classes and jar files.