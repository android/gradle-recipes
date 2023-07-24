# Template recipe

Steps
=====
1. Change the project [name](TemplateRecipe/settings.gradle.kts).
2. Add the Variant API logic to the custom plugin.
    [plugin](TemplateRecipe/build-logic/plugins/src/main/kotlin/CustomPlugin.kt), including tasks to test the API
3. Update the recipe metadata [file](TemplateRecipe/recipe_metadata.toml):
    index entries, tasks and the recipe name.
4. Call the tasks `./gradlew debugTemplateRecipe` or `./gradlew releaseTemplateRecipe`

