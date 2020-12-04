# Artifacts.get in Kotlin

This sample shows how to obtain the bundle file from the AGP.
The [onVariants] block will wire the [DisplayBundleFile] input property (bundleFile) by using
the Artifacts.get call with the right ArtifactType
`bundleFile.set(artifacts.get(ArtifactType.BUNDLE))`
## To Run
./gradlew debugDisplayBundleFile
expected result : "Got the Bundle ...." message.