# Disable tests using the variant API

This sample shows how to disable both unit tests and Android tests on a project using the variant API. These properties
are set on sub-types of [`VariantBuilder`](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/variant/VariantBuilder), for both unit tests and Android tests.

This recipe contains the following directories:

| Module                     | Content                                                     |
|----------------------------|-------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe. |
| [app](app)                 | An Android application that has the plugin applied.         |

The [build-logic](build-logic) sub-project contains the [`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) and [`CheckTestStatusClass`](build-logic/plugins/src/main/kotlin/CheckTestStatusClass.kt).

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) uses the [VariantBuilder] object to disable to the tests on different variants. These properties
are then passed into the [`CheckTestStatusTask`](build-logic/plugins/src/main/kotlin/CheckTestStatusTask.kt) to validate
they are turned off for each variant. Below is an example usage of disabling the Android test type:

```
androidComponents.beforeVariants(androidComponents.selector().withBuildType("debug")) { variantBuilder ->
    (variantBuilder as? HasDeviceTestsBuilder)?.deviceTests?.get(DeviceTestBuilder.ANDROID_TEST_TYPE)?.enable = false
}
```

The variant builder must be a sub-type of `HasDeviceTestsBuilder` here to access the property. In this example, we
query the `deviceTests` property for the Android tests, and disable them for the debug build type.

For host tests, the sub-type `HasHostTestsBuilder` is required, and we query the host test types for the unit test
entry. This is disabled using the following snippet:

```
androidComponents.beforeVariants(androidComponents.selector().withBuildType("release")) { variantBuilder ->
    (variantBuilder as? HasHostTestsBuilder)?.hostTests?.get(HostTestBuilder.UNIT_TEST_TYPE)?.enable = false
}
```

This will disable the unit tests for the release build type.

## To Run
To execute example you need to enter command:

`./gradlew :app:checkDebugTestStatus` and `./gradlew :app:checkReleaseTestStatus`
