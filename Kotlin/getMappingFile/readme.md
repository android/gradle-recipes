# artifacts.get in Kotlin

This sample shows how to obtain the obfuscation mapping file from the AGP.
The [onVariants] block will wire the [MappingFileUploadTask] input property (apkFolder) by using
the [Artifacts.get] call with the right [SingleArtifact.
`mapping.set(artifacts.get(SingleArtifact.OBFUSCATION_MAPPING_FILE))`
## To Run
./gradlew debugMappingFileUpload
expected result : "Uploading .... to a fantasy server...s" message.