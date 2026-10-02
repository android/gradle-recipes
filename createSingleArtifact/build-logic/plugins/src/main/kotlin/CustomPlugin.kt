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
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

/**
 * This custom plugin will use variant API to replace the merged manifest with an output of a custom task.
 * [ProduceAndroidManifestTask] will generate a Android Manifest file which will replace the build generated merged manifest.
 */
class CustomPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        // Registers a callback on the application of the Android Application plugin.
        // This allows the CustomPlugin to work whether it's applied before or after
        // the Android Application plugin.
        project.plugins.withType(AppPlugin::class.java) {
            val androidComponents =
                    project.extensions.getByType(AndroidComponentsExtension::class.java)
            androidComponents.onVariants { variant ->
                val produceManifestTask = project.tasks.register<ProduceAndroidManifestTask>("${variant.name}ProduceAndroidManifest")
                // Wire produceManifestTask task's output to create SingleArtifact.MERGED_MANIFEST
                variant.artifacts.use(produceManifestTask)
                        .wiredWith(ProduceAndroidManifestTask::outputManifest)
                        .toCreate(SingleArtifact.MERGED_MANIFEST)

                // Register a task to verify that the SingleArtifact.MERGED_MANIFEST and the produceManifestTask task's output
                // file's contents are identical.
                val verifyTaskName = "verify${variant.name}Manifest"
                project.tasks.register<VerifyManifestTask>(verifyTaskName) {
                    mergedManifest.set(variant.artifacts.get(SingleArtifact.MERGED_MANIFEST))
                    producedManifest.set(produceManifestTask.flatMap { it.outputManifest })
                    output.set(
                            project.layout.buildDirectory.dir("intermediates/$verifyTaskName")
                    )
                }
            }
        }
    }
}

abstract class VerifyManifestTask : DefaultTask() {

    // In order for the task to be up-to-date when the inputs have not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @get:InputFile
    abstract val mergedManifest: RegularFileProperty

    @get:InputFile
    abstract val producedManifest: RegularFileProperty

    @TaskAction
    fun taskAction() {
        // Verify that the content of `SingleArtifact.MERGED_MANIFEST` is identical to the custom task's output.
        if (!mergedManifest.get().asFile.readBytes()
                        .contentEquals(producedManifest.get().asFile.readBytes())) {
            throw RuntimeException("content of merged manifest is unexpected.")
        }
    }
}