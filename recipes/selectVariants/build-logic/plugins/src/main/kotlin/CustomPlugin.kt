/*
 * Copyright 2022 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import java.util.regex.Pattern

/**
 * This custom plugin shows selector examples for variant API and register task for verifying settings.
 */
class CustomPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        // Registers a callback on the application of the Android Application plugin.
        // This allows the CustomPlugin to work whether it's applied before or after
        // the Android Application plugin.
        project.plugins.withType(AppPlugin::class.java) {

            // Queries for the extension set by the Android Application plugin.
            // This is the second of two entry points into the Android Gradle plugin
            val androidComponents =
                    project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)

            // Below there multiple examples on how to select variants
            // Variants: [Demo, Full][minApi21, minApi24][Debug, Release]

            // Example 1.
            // Init selector as separate value
            val releaseSelector = androidComponents.selector().withBuildType("release")
            // This configures release variants to minify code and resources, the
            // other variants are untouched.
            androidComponents.beforeVariants(releaseSelector) { variantBuilder ->
                variantBuilder.shrinkResources = true
                variantBuilder.isMinifyEnabled = true
            }

            // Example 2.
            // Inits selector in run scope function
            // Set minSdk per api flavor
            androidComponents.run {
                beforeVariants(selector().withFlavor("api" to "minApi24")) { variantBuilder ->
                    variantBuilder.minSdk = 24
                }
            }
            androidComponents.run {
                beforeVariants(selector().withFlavor("api" to "minApi21")) { variantBuilder ->
                    variantBuilder.minSdk = 21
                }
            }

            // Example 3.
            // Selects variants by name
            // Sets enableUnitTest enabled to false
            androidComponents.beforeVariants(
                    androidComponents.selector().withName("fullMinApi24Release")
            ) { variantBuilder ->
                variantBuilder.enableUnitTest = false
            }

            // Registers a callback to be called, when variants will be configured
            androidComponents.onVariants(androidComponents.selector().all()) { variant ->
                // Register CheckConfigurationTask and set input and output properties
                project.tasks.register<CheckConfigurationTask>("check${variant.name}Configuration") {
                    minSdkVersion.set(variant.minSdk.apiLevel)
                    shrinkResources.set(variant.shrinkResources)
                    enableUnitTests.set(variant.unitTest != null)
                    variantName.set(variant.name)
                    output.set(project.layout.buildDirectory.dir("check${variant.name}Configuration"))
                }
            }
        }
    }
}

/**
 * This task verifies that we set minSdk, shrinkResources and enableUnitTest correctly
 */
abstract class CheckConfigurationTask : DefaultTask() {

    // In order of the task to be up-to-date when the data has not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed or not
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @get:Input
    abstract val minSdkVersion: Property<Int>

    @get:Input
    abstract val shrinkResources: Property<Boolean>

    @get:Input
    abstract val enableUnitTests: Property<Boolean>

    @get:Input
    abstract val variantName: Property<String>

    @TaskAction
    fun taskAction() {
        val name = variantName.get().lowercase()

        if (name.contains("release") && shrinkResources.get() == false)
            throw IllegalStateException("Release variants must have shrinkResources enabled")

        // check minSdk for variants
        if (name.contains("minapi24") && minSdkVersion.get() != 24)
            throw IllegalStateException("minApi24 variants must have minSdk = 24")
        if (name.contains("minapi21") && minSdkVersion.get() != 21)
            throw IllegalStateException("minApi21 variants must have minSdk = 21")

        if (variantName.get() == "fullMinApi24Release" && enableUnitTests.get() == true) {
            throw IllegalStateException("fullMinApi24Release variant must have unit tests disabled")
        }
    }
}