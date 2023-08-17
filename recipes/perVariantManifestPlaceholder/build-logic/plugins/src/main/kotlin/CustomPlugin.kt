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

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

/**
 * This custom plugin will register a callback that is applied to all variants.
 * Each variant will register a new placeholder value that will be injected into the
 * manifest during manifest merging and processing.
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

            // Registers a callback to be called, when a new variant is configured
            androidComponents.onVariants { variant ->

                // Registers the placeholder. Its value is variant-specific.
                variant.manifestPlaceholders.put("MyName", "${variant.name}Activity")

                // Setups a verification task to validate the content of the manifest.
                // This is not part of the placeholder API. The goal is to demonstrate
                // that the placeholder API works

                project.tasks.register<ManifestVerifierTask>("${variant.name}ManifestVerifier") {
                    // Gives the merged manifest file to the task. This API, using [RegularFileProperty]
                    // will automatically link the two tasks together to ensure that the merge task
                    // is executed before the verification task
                    manifest.set(variant.artifacts.get(SingleArtifact.MERGED_MANIFEST))
                    activityPrefix.set(variant.name)
                    output.set(
                        project.layout.buildDirectory.dir("intermediates/perVariantManifestPlaceholder/$it.name")
                    )
                }
            }
        }
    }
}

/**
 * Verification task to validate the content of the manifest. This task is not part
 * of the manifest placeholder API. This task here to verify that
 * the API does what is says.
 */
abstract class ManifestVerifierTask : DefaultTask() {

    @get:InputFile
    abstract val manifest: RegularFileProperty

    @get:Input
    abstract val activityPrefix: Property<String>

    // In order of the task to be up-to-date when the manifest has not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed or not
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @TaskAction
    fun taskAction() {
        val manifestText = manifest.asFile.get().readText()

        val expectedActivityName =
            "android:name=\"com.example.android.recipes.per_variant_manifest_placeholder.${activityPrefix.get()}Activity\""
        if (!manifestText.contains(expectedActivityName)
        ) {
            throw RuntimeException("Manifest Placeholder not replaced successfully")
        }
    }
}