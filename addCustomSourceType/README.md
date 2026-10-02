# Add custom source folders

This sample shows how to add a new custom source folders to the Variant for newly create source type.
The source folder will not be used by any AGP tasks (since we do not know about it), however, it can
be used by plugins and tasks participating into the Variant API callbacks.

In this recipe, a custom source type is registered in the app's [build.gradle.kts](app/build.gradle.kts):

```
androidComponents {
    registerSourceType("toml")
}
```

This will register a toml folder in each sourcesets (e.g. src/main/toml, src/debug/toml, etc...)

To access the custom sources, you just need to use
`sourceFolders.set(variant.sources.getByName("toml").getAll())`
which can be used as [Task] input directly.

There are two types of [`SourceDirectories`](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/variant/SourceDirectories):
[`Flat`](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/variant/SourceDirectories.Flat) and [`Layered`](https://developer.android.com/reference/tools/gradle-api/8.0/com/android/build/api/variant/SourceDirectories.Layered).
Custom sources are always of type `Flat`, meaning the directories are stored in type `Provider<Collection<Directory>>`.

To add a folder which content will be generated during execution time by [Task], you need
to use  [SourceDirectories.addGeneratedSourceDirectory] and the pointer to the output folder
where source files will be generated.

You can check sources assignments/manipulation at [`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt)

## To Run

To run the recipe : `./gradlew :app:debugDisplayAllSources`, it will show `debugDisplayAllSources` output with
specific `toml` source type folders
```
> Task :app:debugDisplayAllSources
--> Got a directory src/main/toml
<-- done
--> Got a directory src/debug/toml
<-- done
--> Got a directory third_party/debug/toml
<-- done
--> Got a directory build/generated/toml/debugAddCustomSources
<-- done

BUILD SUCCESSFUL
```