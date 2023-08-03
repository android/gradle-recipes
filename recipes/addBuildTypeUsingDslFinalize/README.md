# Extending AGP DSL.

This recipe show how you can customize the DSL programmatically after the build files are executed but 
before the variants are created.
This facility gives you access to the whole DSL allowing you to set final values, and verify user-set values. 
For example, it can be used to create build types or product flavors programmatically or just set a
value on the DSL extensions without having to put code in the build files.

**If you want to set default, use the normal DSL on plugin application.**  
**If you want to do post-user settings changes or validation, use this DSL.**

In this recipe, we use the `AndroidComponentsExtension.finalizeDsl` to add a new build type called `extra`.
This recipe contains the following directories:

| Module                     | Content                                                                     |
|----------------------------|-----------------------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe.                 |
| [app](app)                 | An Android application that will be configured with the `extra` build type. |

## Details

### DSL Extension registration

The recipe adds a new custom Project plugin called [android.recipes.addBuildTypeUsingDslFinalize](build-logic/plugins/build.gradle.kts)
that is configured by [CustomPlugin.kt](build-logic/plugins/src/main/kotlin/CustomPlugin.kt).

The custom plugin will use the finalizeDsl: 
```
androidComponents.finalizeDsl { extension ->
    extension.buildTypes.maybeCreate("extra").let {
        it.isJniDebuggable = true
    }
}
```

### Run the example

To run the examples, you can just do
```
./gradlew extraVerifyRecipe
```
and the output should be :
```
> Task :app:extraVerifyRecipe
Success : `extra` BuildType successfully created
```