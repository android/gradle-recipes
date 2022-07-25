# Sources.java.getAll in Kotlin
This sample shows how to obtain the java sources and add a [Directory] to the list of java sources
that will be used for compilation.

To access all java sources, you just need to use
`sourceFolders.set(variant.sources.java.all`
which can be used as [Task] input directly.

To add a folder which content will be  a execution time by a [Task] execution, you need
to use the [SourceDirectories.add] method providing a [TaskProvider] and the pointer to the output folder
where source files will be generated and added to the compilation task.

## To Run
./gradlew :app:debugDisplayAllSources
