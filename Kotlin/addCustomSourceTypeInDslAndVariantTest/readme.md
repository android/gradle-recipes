# Add custom source folders in Kotlin
This sample shows how to add a new custom source folders to the Variant. The source folder will
not be used by any AGP tasks, however, it can be used by plugins and tasks participating into the
Variant API callbacks.

To access the custome sources, you just need to use
`sourceFolders.set(variant.sources.getByName("toml").all`
which can be used as [Task] input directly.

To add a folder which content will be  a execution time by a [Task] execution, you need
to use the [SourceDirectories.add] method providing a [TaskProvider] and the pointer to the output folder
where source files will be generated and added to the compilation task.

## To Run
./gradlew :app:debugDisplayAllSources