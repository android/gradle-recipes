# Iterating over all modules in a project and extract produced APKs

The recipe shows how to use a [Settings](https://docs.gradle.org/current/dsl/org.gradle.api.initialization.Settings.html) plugin to iterate over every modules in a project 
and take specific action if the Android application plugin is applied.
When it is applied, the plugin will select the "release" variant APK output and add those to the input of a 
single task. That single 'allProjectsAction` task, will then display the list. This can be  
modified to instead zip up APKs, sign APKs, etc...

This recipe contains the following directories : 

| Module                     | Content                                                           |
|----------------------------|-------------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Settings plugin that is the core of the recipe       |
| [app](app)                 | A small Android application sample used to demonstrate the recipe |
| [app2](app2)               | Another small Android application sample                          |


The build-logic sub-project contains the Plugin called [CustomSettings.kt](build-logic/plugins/src/main/kotlin/CustomSettings.kt) that will
iterate over all sub-project "release" variant.

The [AppProjectsApkTask.kt](build-logic/plugins/src/main/kotlin/AllProjectsApkTask.kt) is the "allProjectsAction" task implementation.

To run the recipe : `gradlew allProjectsAction`