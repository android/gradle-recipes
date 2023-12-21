# How to become a contributor and submit your own code

## Contributor License Agreements

We'd love to accept your patches! Before we can take them, we
have to jump a couple of legal hurdles.

Please fill out either the individual or corporate Contributor License Agreement (CLA).

  * If you are an individual writing original source code and you're sure you
    own the intellectual property, then you'll need to sign an [individual CLA](https://developers.google.com/open-source/cla/individual).
  * If you work for a company that wants to allow you to contribute your work,
    then you'll need to sign a [corporate CLA](https://developers.google.com/open-source/cla/corporate).

Follow either of the two links above to access the appropriate CLA and
instructions for how to sign and return it. Once we receive it, we'll be able to
accept your pull requests.

## Contributing A Patch

All development is done on the `studio-main` branch. You should base any changes from this branch.

1. Submit an issue describing your proposed change to the repo in question.
1. The repo owner will respond to your issue promptly.
1. If your proposed change is accepted, and you haven't already done so, sign a
   Contributor License Agreement (see details above).
1. Fork the desired repo, develop and test your code changes.
1. Ensure that your code adheres to the existing style in the sample to which
   you are contributing. Refer to the
   [Kotlin Style Guide](https://android.github.io/kotlin-guides/style.html) for the
   recommended coding standards for this organization.
1. Submit a pull request targeting the `studio-main` branch.

Note that pull request will not directly be submitted via github. They'll be automatically
copied to our internal CI and submitted there. From there, they'll be automatically replicated
to github.


## Editing or adding a recipe

The recipes are located in the `recipes/` folder. In that location, they are stored in a
format that isn't directly usable. They contain placeholders for various items (AGP version,
Gradle and Kotlin versions, but also `minSdk`, repository locations, etc...)

A tool (`convert-tool`) is used to convert recipes to make edits and to publish recipes.

### Building `convert-tool`

This tool allows you to convert recipes from one mode to another. There are 3 different
modes:
- `source`. This is the source of the recipes, with placeholders.
- `workingcopy`. This mode allows you to edit the recipes, while still being able to convert it back to `source` mode
- `release`. This is the release mode for publication to the `agp-*` branches. All information about the placeholders are lost and it's not possible to convert recipes back into `source` mode.

To build the tool:
```
cd convert-tool
./gradlew install
cd ../
```

To run the tool from the root folder, you can use `convert.sh`.


### Editing a recipe

Editing a recipe in Android Studio requires converting a recipe into `workingcopy` mode:

```
./convert.sh --mode workingcopy --source recipes/<name> --destination workingcopies/
```

This will convert the recipe into a new folder `workingcopies/<name>`. You can now open
this recipes in Android Studio.

Note that the recipe will be created using its minimum AGP version (and associated Gradle
version). You can find this info inside `recipe_metadata.toml`

While you should test your changes locally, you will also need to validate your recipe before
converting it back to `source` mode.


### Validating a recipe

The validation step ensures that the recipe works on all the versions of AGP that it
declares being compatible with. This is done by converting the recipe back to `source`
mode, and then converting it to specific AGP version (in `release` mode)

Inside `recipe_metadata.toml`, there is a section about AGP versions:
```
[agpVersion]
min = "8.1.0"
max = "8.4.0"
```

This indicates the range that the recipe is compatible with. The `max` value is optional.


```
./convert.sh validate -m workingcopy -s workingcopies/<name>
```

This will run the validation steps for both the minimum declared version of AGP, and either
the max version, or the current latest version. The current latest version is the last
entry in the `version_mappings.txt` file.

Validation is done by running tasks declared in the `recipe_metadata.toml` file:
```
[gradleTasks]
tasks = [
    "extraVerifyRecipe",
    "assembleExtra"
]
```

On Google's internal CI, the recipe is also validated against all versions of AGP declared
`version_mappings.txt` that are between the min and max or latest.

**Important**: If you want to introduce usages of recipe that require introducing or modifying
the `max` value of `agpVersion`, you need to first file a ticket and discuss this with the
team. It's very likely that the recipe will have to be forked to support versions of AGP
that the recipe stops being compatible with.

### Converting back to `source` mode

Once the recipe is updated and validated, it can be converted back to its `source` state.


```
./convert.sh --mode source --source workingcopies/<name> --destination recipes/ --overwrite
```

The `--overwrite` option is necessary unless you delete `recipes/<name>` first. This will
clear the folder before converting the recipe.


### Adding a new recipe

To get started adding a new recipe, we have 2 different templates:
- `templates/TemplateRecipe`: template for `Project` plugins
- `templates/TemplateSettingsRecipe`: template for `Settings` plugins

First, create a working copy:
```
./convert.sh --mode workingcopy --source templates/TemplateRecipe --destination workingcopies/
```

Then, write your recipe, generally starting with the plugin class (`CustomPlugin.kt` or `CustomSettings.kt`).

You will also need to
- rename the folder name from `TemplateRecipe` to something more relevant. This needs to be unique but is not the recipe title
- edit `recipe_metadata.toml`:
  - edit the title. This is the name of the recipe published in the index. You can have multiple recipe with the same name as long as they don't overlap in the AGP versions they support.
  - edit the description
  - edit the min/max version of AGP that is supported
  - provide a list of tasks that validate that the recipe is working
  - provide index entries. Look at existing values in an `agp-*` branch.

