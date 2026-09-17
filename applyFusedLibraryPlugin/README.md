# Fused Library Plugin

## What is Fused Library Plugin?

An Android Gradle Plugin that helps Android library developers publish multiple Android libraries in a
single Android library artifact i.e. the '.aar' file.

### *IMPORTANT* - State of the Fused Library Plugin
Fused Library Plugin is currently in an early testing phase. Therefore, artifacts published by the
plugin and plugin behaviour may not be stable at this time. Take caution before distributing
published artifacts created by the plugin; there is no guarantee of correctness.

As an early adopter, please be aware that there may be frequent breaking changes that may require
you to make changes to your project.

The recipe may not be updated as frequently as the plugin, so please ensure you are using the most
recent version of Android Studio Canary and the Android Gradle Plugin alpha releases.

### This recipe

This project aims to provide a non-exhaustive, but common examples of plugin usage for consumption
by other libraries.
<pre>
┌─────────────────────────────────────────┐
│             :app                        │
│               ▲                         │
│               │                         │
│         :fusedLibrary                   │
│         ▲           ▲                   │
│         │           │                   │
│    :androidLib2*  :androidLib1*         │
│         ▲            ▲                  │
│         │            │                  │
│ :androidLib3 com.google.code.gson:gson* │
└─────────────────────────────────────────┘
</pre>
This diagram shows an overview of the relevant project dependency structure.

`*` indicates an `include` dependency of the `:fusedLibrary` module

Example usages of classes, resources and other artifacts are demonstrated in the :app module unit
and instrumentation tests.

## Usage Guide

### Setting up the Fused Library

*This recipe project was set up with the following key steps*

1. Apply the plugin
   gradle/libs.versions.toml append
```toml
[plugins]
android-fusedlibrary = { id = "com.android.fusedlibrary", version.ref = "agp" }
```
2. Create a new module. `File` > `New Module...` . Then, click `Android Library` and fill out the
   required module metadata. Click `Finish`.
3. In the new module (let's call it `:fusedLibrary`) open the `build.gradle.kts` file,
   then replace the `plugins` block with
```kts
plugins {
    alias(libs.plugins.android.fusedlibrary)
}
```
to apply the Fused Library Plugin
4. Fused library modules cannot not contain sources such as code or resources, nor does it use
   the typical `implementation` or `api` configurations you may expect to declare as dependencies.

Done.

Fused library introduces a new configuration `include`, that declares what dependencies will be
fused in the built/published .aar file.

For example, the `:fusedLibrary` could define the following in the `dependencies` block:

```kts
dependencies {
    include(project(":androidLib1"))
    include(project(":androidLib2"))
    include("com.google.code.gson:gson:2.11.0")
    include(files("libs/simple-jar-with-A_DoIExist-class.jar"))
}
```

### Building the fused library

1. Check what dependencies will be included in the .aar based on the dependencies configuration
   by running `./gradlew :fusedLibrary:report`
   Take a look at the report output in the :fusedLibrary build directory
   `fusedLibrary/build/reports/fused_library_report/single/report.json`. Check the dependencies that
   will be included in the library match your expectations.
2. Once you are satisfied, you can proceed to build the library using
   `./gradlew :fusedLibrary:assemble`. Assuming dependencies are valid,
   this task produces the .aar fused library at `fusedLibrary/build/bundle/bundle.aar`.
3. Resync project `ctrl+shift+O`

Done.

At this point, you can add the fused library as a dependency from other modules.

### Running the consumption tests

In the :app module there are tests that make use of the classes and resource distributed via 
`:fusedLibrary`.

Run unit tests: `./gradlew :app:testDebugUnitTest --tests "com.example.fusedlibrarysample.FusedLibraryConsumptionUnitTest"`

Run instrumentation tests: `./gradlew :app:connectedDebugAndroidTest`

### Publishing the fused library

Fused Library Plugin artifacts can be easily configured for publication with Maven publishing
plugins. The plugin generates it's own POM for distribution that preserves the artifact dependencies.
We'll provide some typical configurations for publishing that may be useful for most use cases
however, if your needs are more complex, consult the Maven documentation.

Generating the fused lib POM
1. Follow the steps of `Building a fused library`
2. `./gradlew :fusedLibrary:generatePomFileForMavenPublication`
3. The POM should be created at `fusedLibrary/build/publications/maven/pom-default.xml`
Done.

Generating a maven repository with the fused library
1. Add configuration to the fused library build file
```
   publishing {
      publications {
         register<MavenPublication>("release") {
            // Customize with your own publication metadata
            groupId = "com.my-company"
            artifactId = "my-fused-library"
            version = "1.0"

            afterEvaluate {
                // fusedLibraryComponent is required for obtaining fused library artifacts
                from(components["fusedLibraryComponent"])
            }
        }
   }
   repositories {
      maven {
         name = "myrepo"
         url = uri(layout.buildDirectory.dir("repo"))
     }
   }
 }

```
2. Execute the task for creating the repository `./gradlew :fusedLibrary:publishReleasePublicationToMyrepoRepository`
3. As androidLib3 is a project dependency of the fused library, that also needs to be published to 
the repository`./gradlew :androidLib3:publishMavenPublicationToMyrepoRepository`
4. Note: `:app` has already configured dependency substitution that prefers the published local repo 
artifacts over the `:fusedLibrary` project itself, so `:app` now automatically depends on the correct 
artifacts.

Done.

### Configurations

| Name    | Description                                                                                                                                                                                                                                                                                                               |
|---------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| include | Specifies components to be fused into the final artifact. Included components are resolvable at runtime. This configuration is not transitive, therefore dependencies will not be included in the fused artifact. There is no support for file dependencies excluding libs/jars. Databinding dependencies are prohibited. |

### Report Issues

This plugin remains in early stages, and there may be corner cases that have not been fully tested
or developed.

See open public issues at this link [open issues](https://issuetracker.google.com/components/1692458)

Follow the below steps and use this [link to **file new bugs**](https://issuetracker.google.com/issues/new?title=%5Bfused+lib+-+public%5D+%3CIssue+Name+Here%3E&cc=lukeedgar%40google.com%2C+android-gradle%40google.com&description=1.+Steps+to+reproduce%0A2.+A+paste+of+the+exception%0A3.+run+%60.%2Fgradlew+%3A%3Cfused+library+module%3E%3Areport%60+and+paste+the+contents+of+%0A%60%3Cmy+library+module%3Ebuild%2Freports%2Ffused_library_report%2Fsingle%2Freport.json%60%0A4.+Also+consider+running+%60.%2Fgradlew+%3A%3Cfused+library+module%3E%3Adependencies%60+if+dependency+information+is+relevant%0A5.+%5C%5Boptional%5C%5D+if+the+build+was+successful%2C+provide+a+copy+of+the+.aar&format=MARKDOWN&component=192708&type=BUG&priority=P2&severity=S2&hotlistIds=4053459&assignee=lukeedgar%40google.com)
**or provide suggestions** for the Fused Library Plugin.

When filing an issue, please include the following information:
1. Steps to reproduce
2. A paste of the exception
3. run `./gradlew :<fused library module>:report` and paste the contents of 
`<my library module>build/reports/fused_library_report/single/report.json`
4. Also consider running `./gradlew :<fused library module>:dependencies` if dependency information is relevant
5. \[optional\] if the build was successful, provide a copy of the .aar
Done.
