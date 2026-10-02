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
import com.android.build.api.artifact.SingleArtifact
import com.android.build.gradle.AppPlugin
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import java.lang.IllegalStateException

/**
 * This custom plugin will register a task output as a generated source folder for
 * android Assets.
 *
 * It will also create a Task to verify that the generated sources are properly
 * accounted for during building.
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
                variant.sources.assets
                    ?.let {
                        // create the task that will copy new source files to the asset source folder.
                        val propertyBasedCopyTaskProvider =
                            project.tasks.register<PropertyBasedCopy>("create${variant.name}Asset")

                        propertyBasedCopyTaskProvider.configure { task: PropertyBasedCopy ->
                                task.from("src/common")
                                task.include("**/*asset*.*")
                            }

                        // registers the newly created Task as the provider for a new generated
                        // source folder for the 'assets' type.
                        // The task will execute only when the `assets` source folders are looked
                        // up at execution time (during asset merging basically).
                        it.addGeneratedSourceDirectory(
                            propertyBasedCopyTaskProvider,
                            PropertyBasedCopy::outputDirectory
                        )
                    }

                // create the verification task
                project.tasks.register<VerifyAssetTask>("${variant.name}VerifyAsset") {
                    output.set(
                        project.layout.buildDirectory.dir("intermediates/recipe/$it.name")
                    )
                    // the verifying task will look at the merged assets folder and ensure
                    // the file added by the assetCreationTask is present.
                    assets.set(variant.artifacts.get(SingleArtifact.ASSETS))
                }
            }
        }
    }
}

/**
 * This task is copying files.
 *
 * It is based on the Gradle's [org.gradle.api.tasks.Copy] task and bridge the
 * `destinationDir` output to a `DirectoryProperty` that can be used with the
 * Variant APIs.
 */
abstract class PropertyBasedCopy: Copy() {

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    override fun getDestinationDir(): File =
        outputDirectory.get().asFile

    override fun setDestinationDir(destination: File) {
        outputDirectory.set(destination)
    }
}

/**
 * This task here to verify that the API does what is says.
 */
abstract class VerifyAssetTask : DefaultTask() {

    // In order of the task to be up-to-date when the input has not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed or not
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @get:InputDirectory
    @get:Optional
    abstract val assets: DirectoryProperty

    @TaskAction
    fun taskAction() {
        if (!File(assets.get().asFile, "custom_asset.txt").exists()) {
            throw RuntimeException("custom_asset.txt file not present in merged asset folder: " +
                "${assets.get().asFile}")
        }
    }
}