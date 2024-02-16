# Add custom source folders

This sample shows how to add a new custom source folders to the Variant for newly create source type.
The source folder will not be used by any AGP tasks (since we do not know about it), however, it can
be used by plugins and tasks participating into the Variant API callbacks.

To access the custom sources, you just need to use
`sourceFolders.set(variant.sources.getByName("toml").getAll())`
which can be used as [Task] input directly.

To add a folder which content will be generated during execution time by [Task], you need
to use  [SourceDirectories.addGeneratedSourceDirectory] and the pointer to the output folder
where source files will be generated.

You can check sources assignments/manipulation at [`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt)

## To Run

To run the recipe : `./gradlew :app:sourceSets :app:debugDisplayAllSources`
and the output will show all possible source sets with source types folders:
```
...
release
-------
Compile configuration: releaseCompile
build.gradle name: android.sourceSets.release
Java sources: [app/src/release/java]
Kotlin sources: [app/src/release/kotlin, app/src/release/java]
Manifest file: app/src/release/AndroidManifest.xml
Android resources: [app/src/release/res]
Assets: [app/src/release/assets]
AIDL sources: [app/src/release/aidl]
RenderScript sources: [app/src/release/rs]
Baseline profile sources: [app/src/release/baselineProfiles]
JNI sources: [app/src/release/jni]
JNI libraries: [app/src/release/jniLibs]
Custom sources: [app/src/release/toml]
Java-style resources: [app/src/release/resources]

test
----
Compile configuration: testCompile
build.gradle name: android.sourceSets.test
Java sources: [app/src/test/java]
Kotlin sources: [app/src/test/kotlin, app/src/test/java]
Java-style resources: [app/src/test/resources]
...
```
also it will show `debugDisplayAllSources` output with specific `toml` source type folders
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