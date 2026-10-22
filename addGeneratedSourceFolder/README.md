# Adding source folder with dynamic content

This recipe shows how you can add a source folder to the set of source folders for a specific type of
android source files. In this example, we add a source folder to Android's `assets`. The source folder
content is provided by a Task and it is therefore dynamic.

In the Variant API, the source folders are accessible through the `Component.sources` method. This `sources`
will provide access to all the variant's source folders for each source type.

Therefore, other types of android source files can be extended in a similar way like java, kotlin, java resources,
android resources, shaders, etc... See [`Component.sources`](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/variant/Sources) for the complete list.

There are two types of [`SourceDirectories`](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/variant/SourceDirectories):
[`Flat`](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/variant/SourceDirectories.Flat) and [`Layered`](https://developer.android.com/reference/tools/gradle-api/8.0/com/android/build/api/variant/SourceDirectories.Layered).
In this recipe, we use `assets`, which are of type `Layered`, meaning the directories are stored in type
`Provider<List<Collection<Directory>>>`.

We use `SourceDirectories.addGeneratedSourceDirectory` to add a new folder for `assets` processing. You can extrapolate
the same mechanism for other types of source files.

| Module                     | Content                                                                      |
|----------------------------|------------------------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe.                  |
| [app](app)                 | An Android application that will be configured with the added source folder. |

## Details

### Dynamically generated sources

When you need to generate source files dynamically (based on other source files for instance), you should do so
in a Task with proper Input and Output declarations so you get correct up-to-date checks and caching.
You will find `AssetCreatorTask` as an example of such a task in the
[CustomPlugin.kt](build-logic/plugins/src/main/kotlin/CustomPlugin.kt).

Once the `TaskProvider` is created, you need to use `SourceDirectories.addGeneratedSourceDirectory` to register its
output as a new source folder.
```
variant.sources.assets?.addGeneratedSourceDirectory(
    assetCreationTask,
    AssetCreatorTask::outputDirectory)
```

### Run the example

To run the examples, you can just do:

```
./gradlew :app:verifyDebugAsset
```
