/*
 * Copyright 2024 The Android Open Source Project
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
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.VariantOutputConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.gradle.configurationcache.extensions.capitalized

/**
 * This custom plugin will register a callback that is applied to all variants.
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

            // Registers flavor-specific callbacks, using the selector, to update version properties from the variant
            // outputs for two different flavors
            androidComponents.onVariants(androidComponents.selector().withFlavor("dimension1", "flavor1")) { variant ->
                // There are multiple variant outputs depending on the type of APK produced
                // Here and below, multi-APK is not enabled, so we expect to find a single output with type SINGLE,
                // which is associated with the main APK
                val variantOutput = variant.outputs.first {
                    it.outputType == VariantOutputConfiguration.OutputType.SINGLE
                }
                variantOutput.versionName.set("updatedFlavor1DebugVersionName")
            }
            androidComponents.onVariants(androidComponents.selector().withFlavor("dimension1", "flavor2")) { variant ->
                val variantOutput = variant.outputs.first {
                    it.outputType == VariantOutputConfiguration.OutputType.SINGLE
                }
                variantOutput.versionCode.set(2)
            }

            // -- Verification --
            // the following is just to validate the recipe and is not actually part of the recipe itself
            androidComponents.onVariants(androidComponents.selector().all()) { variant ->
                val taskName = "check${variant.name.capitalized()}MergedManifest"
                project.tasks.register<CheckMergedManifestTask>(taskName) {
                    mergedManifest.set(
                        variant.artifacts.get(SingleArtifact.MERGED_MANIFEST)
                    )
                    variantName.set(variant.name)
                    output.set(project.layout.buildDirectory.dir("intermediates/$taskName"))
                }
            }
        }
    }
}
