# Public txt get in Kotlin

This sample show how to obtain the file listing the public artifacts from the Android Gradle Plugin.
The [onVariants] block will wire the [PublicResourcesValidatorTask] input property
(publicAndroidResources) by using
the [Artifacts.get] call with the right [ArtifactType].

```publicAndroidResources.set(artifacts.get(ArtifactType.PUBLIC_ANDROID_RESOURCES_LIST))```

For more information about how to mark resources as public see
[Choose resources to make public](https://developer.android.com/studio/projects/android-library.html#PrivateResources)

## To Run
./gradlew validateDebugPublicResources
expected result : "Public Android resources unchanged."