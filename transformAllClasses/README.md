# Transform classes with ScopedArtifactsOperation.toTransform()

This sample shows how to transform classes that will be used to create the `.dex` files.
There are two lists that need to be used to obtain the complete set of classes because some
classes are present as .class files in directories and others are present in jar files.
Therefore, you must query both [ListProperty] of [Directory] (classes) and [RegularFile]
(jars) to get the full list.

In this example, we query all classes to invoke some bytecode instrumentation on them.

The Variant API provides a convenient API to transform bytecodes based on ASM but this example
is using javassist to show how this can be done using a different bytecode enhancer.

Example deals with classes as `scoped artifacts`. Scoped artifacts are artifacts that can
be made available in the current variant scope, or may be optionally include the project's
dependencies in the results. Scoped artifacts can be classes or Java resources.

The [onVariants] block will create the [ModifyClassesTask] provider with input properties `allJars`,
`allDirectories` and the `output` jar file. `ScopedArtifactsOperation.toTransform()`
wires together transformation type [ScopedArtifact.CLASSES], input files and directories, output
to make scoped artifact transformer.

## To Run
To execute example you need to enter command:

`./gradlew :app:assembleDebug`

You will see output similar to following:

```
> Task :app:debugModifyClasses
handling .../app/build/intermediates/compile_and_runtime_not_namespaced_r_class_jar/debug/processDebugResources/R.jar
Adding from jar com/example/android/recipes/transform_classes/R.class
handling .../app/build/tmp/kotlin-classes/debug
Found .../app/build/tmp/kotlin-classes/debug/com/example/android/recipes/sample/SomeSource.class.name
Adding javassist.CtNewClass@3c77f2f5[hasConstructor changed public abstract interface class com.example.android.recipes.sample.SomeInterface fields= constructors= methods=]

```


