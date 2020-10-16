# artifacts.get in Kotlin

This sample shows how to obtain the aar from the AGP.
The [onVariantProperties] block will wire the [AarUploadTask] input property (apkFolder) by using
the [Artifacts.get] call with the right [ArtifactType]
`aar.set(artifacts.get(ArtifactType.AAR))`
## To Run
/path/to/gradle debugAarUpload
expected result : "Uploading .... to a fantasy server...s" message.