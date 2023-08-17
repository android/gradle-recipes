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
import com.android.build.api.variant.BuiltArtifactsLoader
import com.android.build.api.artifact.MultipleArtifact
import com.android.build.api.artifact.SingleArtifact
import com.android.build.gradle.AppPlugin
import java.io.File
import java.util.jar.JarFile
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
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
                        // create the task that will add new source files to the asset source folder.
                        val assetCreationTask =
                            project.tasks.register<AssetCreatorTask>("create${variant.name}Asset")

                        // registers the newly created Task as the provider for a new generated
                        // source folder for the 'assets' type.
                        // The task will execute only when the `assets` source folders are looked
                        // up at execution time (during asset merging basically).
                        it.addGeneratedSourceDirectory(
                            assetCreationTask,
                            AssetCreatorTask::outputDirectory
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
 * This task is creating an asset that will be used as a source asset file.
 */
abstract class AssetCreatorTask: DefaultTask() {
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun taskAction() {
        outputDirectory.get().asFile.mkdirs()
        File(outputDirectory.get().asFile, "custom_asset.txt")
            .writeText("some real asset file")
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
        File(assets.get().asFile, "custom_asset.txt").let {
            if (it.exists()) {
                println("Found ${it} in merged assets folder")
            } else {
                throw IllegalStateException("custom_asset.txt file not " +
                        "present in merged asset folder :  ${assets.get().asFile}")
            }
        }
    }
}