# artifacts.get in Kotlin

This sample shows how to obtain the obfuscation mapping file from the AGP.
The [onVariantProperties] block will wire the [MappingFileUploadTask] input property (apkFolder) by using
the [Artifacts.get] call with the right [ArtifactType]
`mapping.set(artifacts.get(ArtifactType.OBFUSCATION_MAPPING_FILE))`
## To Run
/path/to/gradle debugMappingFileUpload
expected result : "Uploading .... to a fantasy server...s" message.