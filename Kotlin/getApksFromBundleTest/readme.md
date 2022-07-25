# artifacts.get in Kotlin
This sample shows how to obtain a universal APK from the AGP. Because it goes through the bundle
file, it is a slower build flow than using the [SingleArtifact.APK] public artifact.
The built artifact is identified by its [SingleArtifact and in this case, it's [SingleArtifact.APK_FROM_BUNDLE].
The [onVariants] block will wire the [DisplayApkFromBundle] input property (apkFromBundle) by using
the [Artifacts.get] call with the right [SingleArtifact.
`apkFromBundle.set(artifacts.get(SingleArtifact.APK_FROM_BUNDLE))`
## To Run
./gradlew debugDisplayApkFromBundle
expected result : "Got an Universal APK...." message.