        plugins {
                id("com.android.application")
                kotlin("android")
        }

        android {
                namespace = "com.android.build.example.minimal"
compileSdkVersion(29)
defaultConfig {
    minSdkVersion(21)
}
        }

        androidComponents {
            onVariants(selector().withBuildType("release")) { variant ->
                // components contains the variant and all of its nested components.
                variant.components.forEach { component ->
                    // configure compile and runtime configurations in the same way.
                    listOf(
                        component.compileConfiguration,
                        component.runtimeConfiguration
                    ).forEach { configuration ->
                        configuration.resolutionStrategy.dependencySubstitution {
                            substitute(project(":lib1")).using(project(":lib1Sub"))
                        }
                    }
                }

                // nestedComponents contains the variant's nested components, but
                // not the release variant itself
                variant.nestedComponents.forEach { component ->
                    // configure compile and runtime configurations in the same way.
                    listOf(
                        component.compileConfiguration,
                        component.runtimeConfiguration
                    ).forEach { configuration ->
                        configuration.resolutionStrategy.dependencySubstitution {
                            substitute(project(":testLib")).using(project(":testLibSub"))
                        }
                        configuration.resolutionStrategy.dependencySubstitution {
                            substitute(project(":lib2")).using(project(":lib2Sub"))
                        }
                    }
                }
            }
        }

        dependencies {
            implementation(project(":lib1"))
            implementation(project(":lib2"))
            testImplementation(project(":lib1"))
            testImplementation(project(":lib2"))
            testImplementation(project(":testLib"))
            androidTestImplementation(project(":lib1"))
            androidTestImplementation(project(":lib2"))
            androidTestImplementation(project(":testLib"))
        }