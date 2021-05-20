# artifacts.get in Kotlin
This sample show how to obtain a built artifact from the AGP. The built artifact is identified by
its [SingleArtifact. and in this case, it's [SingleArtifact.APK].
The [onVariants] block will wire the [DisplayApksTask] input property (apkFolder) by using
the [Artifacts.get] call with the right [SingleArtifact.
`apkFolder.set(artifacts.get(SingleArtifact.APK))`
Since more than one APK can be produced by the build when dealing with multi-apk, you should use the
[BuiltArtifacts] interface to load the metadata associated with produced files using
[BuiltArtifacts.load] method.
`builtArtifactsLoader.get().load(apkFolder.get())'
Once loaded, the built artifacts can be accessed.
## To Run
./gradlew debugDisplayApks
expected result : "Got an APK...." message.