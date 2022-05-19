# artifacts.get in Kotlin
This sample show how to obtain the java sources and add a [Directory] to the list of java sources
that will be used for compilation.

To access all java sources, you just need to use
`sourceFolders.set(variant.sources.java.all`
which can be used as [Task] input directly.

To add a folder and its content to the list of folders used for compilation, you need
to use the [SourceDirectories.srcDir] family of methods

## To Run
./gradlew :app:debugDisplayAllSources
