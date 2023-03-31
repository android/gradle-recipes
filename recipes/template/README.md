# Template

Insert here a description of the recipe.

This recipe contains the following directories :

| Module                     | Content                                                                                                                                                        |
|----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Settings plugin that is the core of the recipe. |
| [app](app) | A simplistic Android application module that can be used in the recipe |

## Instructions

1. Copy this directory to a new folder with a descriptive name.
2. Modify the [build.gradle.kts](build-logic/plugins/build.gradle.kts) to provide a name in sync with the recipe
    ```kotlin
    gradlePlugin {
        plugins {
            create("CustomSettingsPlugin") {
                id = "android.recipes.template"
                implementationClass = "CustomSettings"
            }
        }
    }
    ```
3. Change [settings.gradle.kts](settings.gradle.kts) to change the project root name and apply the plugin defined in (2)
4. Change [CustomSettings.kt](build-logic/plugins/src/main/kotlin/CustomSettings.kt) to fit your needs.
5. Change [recipe_metadata.toml](recipe_metadata.toml) to add correct description and keywords, and remember to change the target task name for your recipe
    ```toml
    # Relevant Gradle tasks to run per recipe
    [gradleTasks]
    tasks = [
      "unknown",
    ]
    ```
6. Change namespace in [app/build.gradle.kts](app/build.gradle.kts)
7. Explain what it does [here](README.md) and in the code.