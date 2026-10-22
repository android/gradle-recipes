# Bridging a Legacy Task with new Variant API Property<> instances

This recipe shows how you can adapt a built-in Gradle `Task` using intrinsic file types like `File` in order to be
compatible with the new Variant API. The new variant API requires using instances of `Property<>` when wiring
things up in order to carry task dependency within those property objects. This is not easy when you want to use
an old task expressing its input or output using `File` for instance.

In this example, we add a source folder to Android's `assets`. The source folder
content is provided by a [org.gradle.api.tasks.Copy](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.Copy.html)
Task which expresses its output folder using a
[File](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.Copy.html#org.gradle.api.tasks.Copy:destinationDir)

In this recipe, we use the `SourceDirectories.addGeneratedSourceDirectory` to add a new folder for `assets`
processing using Gradle's `Copy` Tasks.

| Module                     | Content                                                                      |
|----------------------------|------------------------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe.                  |
| [app](app)                 | An Android application that will be configured with the added source folder. |

## Details

### Bridging File to DirectoryProperty

When you need to bridge a Task output expressed using a [File](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.Copy.html#org.gradle.api.tasks.Copy:destinationDir) 
to a Provider<Directory> which is expected by the variant API, you cannot just create a `DirectoryProperty` using 
Gradle's 'ObjectFactory' and call `set()` with the File instance. Although the value would be set correctly, the
Property object would not carry the Task dependency which would eventually yield to a failure :

```
':projectA:someTask' uses this output of task ':projectA:generatingAssetTask' without declaring an explicit or
implicit dependency. This can lead to incorrect results being produced, depending on what order the tasks are executed.
```

In order for gradle to set the property object correctly, you must do a [map](https://docs.gradle.org/current/javadoc/org/gradle/api/provider/Provider.html#map-org.gradle.api.Transformer-)
or [flatMap](https://docs.gradle.org/current/javadoc/org/gradle/api/provider/Provider.html#flatMap-org.gradle.api.Transformer-)
using the `TaskProvider` of the generating Task.

The easiest way to do that is to subclass the original Task and override the right methods to redirect the input or
output values to properties. In our case, the subclass is simply :

```
abstract class PropertyBasedCopy: Copy() {

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    override fun getDestinationDir(): File =
        outputDirectory.get().asFile

    override fun setDestinationDir(destination: File) {
        outputDirectory.set(destination)
    }
}
```

Once the `TaskProvider` is created, you need to use `SourceDirectories.addGeneratedSourceDirectory` to register its
output as a new source folder.
```
variant.sources.assets?.addGeneratedSourceDirectory(
                    propertyBasedCopyTaskProvider,
                    PropertyBasedCopy::outputDirectory)
```

### Run the example

To run the examples, you can just do:
```
./gradlew debugVerifyAsset
```
