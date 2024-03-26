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

import com.android.build.api.artifact.MultipleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.register

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
            // Registers a callback to be called, when a new variant is configured
            androidComponents.onVariants { variant ->
                // -- Setup --
                // the following is done for the sake of the recipe only, in order to add directories to
                // MultipleArtifact.NATIVE_DEBUG_METADATA so that they can be combined in TransformMultipleTask
                variant.artifacts.addStaticDirectory(
                    MultipleArtifact.NATIVE_DEBUG_METADATA,
                    project.layout.projectDirectory.dir("nativeDebugMetadataDir0")
                )
                variant.artifacts.addStaticDirectory(
                    MultipleArtifact.NATIVE_DEBUG_METADATA,
                    project.layout.projectDirectory.dir("nativeDebugMetadataDir1")
                )

                val taskProvider = project.tasks.register<TransformNativeDebugMetadataTask>(
                    "transform${variant.name.capitalized()}NativeDebugMetadata"
                )

                // TransformNativeDebugMetadataTask will combine the files of the input directories into the output
                // directory, which will represent the artifact after transformation.
                variant.artifacts.use(taskProvider)
                    .wiredWith(
                        TransformNativeDebugMetadataTask::inputDirectories,
                        TransformNativeDebugMetadataTask::outputDir
                    ).toTransform(MultipleArtifact.NATIVE_DEBUG_METADATA)

                // -- Verification --
                // the following is just to validate the recipe and is not actually part of the recipe itself
                val taskName = "check${variant.name.capitalized()}NativeDebugMetadata"
                project.tasks.register<CheckNativeDebugMetadataTask>(taskName) {
                    nativeDebugMetadataDirs.set(
                        variant.artifacts.getAll(MultipleArtifact.NATIVE_DEBUG_METADATA)
                    )
                    output.set(project.layout.buildDirectory.dir("intermediates/$taskName"))
                }
            }
        }
    }
}