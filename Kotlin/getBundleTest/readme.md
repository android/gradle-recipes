# Artifacts.get in Kotlin

This sample shows how to obtain the bundle file from the AGP.
The [onVariants] block will wire the [DisplayBundleFile] input property (bundleFile) by using
the Artifacts.get call with the right SingleArtifact
`bundleFile.set(artifacts.get(SingleArtifact.BUNDLE))`
## To Run
./gradlew debugDisplayBundleFile
expected result : "Got the Bundle ...." message.