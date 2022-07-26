# Add custom source folders in Kotlin
This sample shows how to add a new custom source folders to all source sets. The source folder will
not be used by any AGP tasks (since we do no know about it), however, it can be used by plugins and
tasks participating into the Variant API callbacks.

To register the custom sources, you just need to use
`androidComponents { registerSourceType("toml") } `

## To Run
./gradlew sourceSets