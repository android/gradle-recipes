# Customizing test dependencies with dependencySubstitution of variant API

The recipe shows how to use variant API to get direct access to the variant-specific compile and
runtime configurations. It iterates over variants, choose "release" one,
and set dependency substitution for all its components. Recipe substitute dependency of one module (lib)
with another one (libSub).

This example shows how to apply Gradle substitute API ([link](https://docs.gradle.org/current/userguide/resolution_rules.html#sec:dependency_substitution_rules))
for android variant configurations.

This recipe contains the following directories :

| Module                   | Content                                                                             |
|--------------------------|-------------------------------------------------------------------------------------|
| [app](app)               | A small Android application sample used to demonstrate the recipe                   |
| [lib1](lib1)             | Library that `app` depends on                                                       |
| [lib1Sub](lib1Sub)       | Library that we'll use for substitution in all components of release variant        |
| [lib2](lib2)             | Another library that `app` depends on                                               |
| [lib2Sub](lib2Sub)       | Library that we'll use for substitution in all nested components of release variant |
| [testLib](testLib)       | Another Library that `app` depends on                                               |
| [testLibSub](testLibSub) | Library that we'll use for substitution in all nested components of release variant |

## Details
Main configuration script locates in application [build.gradle.kts](app/build.gradle.kts)
inside standard variant selection callback.
```
 onVariants(selector().withBuildType("release")) { variant -> ... }
```
Script substitutes "lib1" using "lib1:Sub" like in following simplified snippet.
```
variant.components.forEach { component ->
   component.compileConfiguration.resolutionStrategy.dependencySubstitution {
            substitute(project(":lib1")).using(project(":lib1Sub"))
        }
    }
}
```
Build script [build.gradle.kts](app/build.gradle.kts) also contains an example for substitution through nestedComponents.


### Run the example
To run the examples, you can just do:
```
./gradlew :app:assemble
./gradlew  :app:testDebugUnitTest
./gradlew  :app:testReleaseUnitTest
./gradlew  :app:assembleAndroidTest
```
The build should be successful and `Sub` modules will be used as library dependencies in tests.
