# bundleConfig.addMetadataFile in Kotlin
This sample shows how to add a metadata file to the built bundle.
The [BundleConfig] variant object will be used to register the output of the AddMetadataInBundleTask
Task to a new metadata file to be added to the resulting bundle file.
## To Run
./gradlew debugDisplayBundle
expected result : You should see the added metadata.pb file added to the resulting bundle.