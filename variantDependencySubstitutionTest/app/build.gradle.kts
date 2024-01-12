/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.android.recipes.variantDependencySubstitutionTest.app"
    compileSdk = 34
    defaultConfig {
       minSdk = 21
       targetSdk = 34
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
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
