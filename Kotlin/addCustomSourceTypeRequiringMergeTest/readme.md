# Add custom source folders in Kotlin
This sample shows how to add a new custom source folders to all source sets. The source folder will
by any AGP tasks (since we do no know about it), however, it can be used by plugins and
tasks participating into the Variant API callbacks.

In this example, it is assumed that a merging activity has to happen before the source folders can
be used to be added to an AGP artifact (like ASSETS for example).

To register the custom sources, you just need to use
`androidComponents { registerSourceType("toml") } `

The merging activity is implemented by the :app:debugMergeTomlSources and the downstream task that
 uses the merged folder is :app:debugConsumeMergedToml

## To Run
./gradlew sourceSets