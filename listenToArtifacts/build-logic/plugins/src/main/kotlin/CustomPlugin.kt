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
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
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

                // create a task that will be responsible for copying the APKs
                val copyTask = project.tasks.register<CopyApk>("copyApksFor${variant.name}") {

                    // set the output only. the input will be automatically provided via the
                    // wiring mechanism
                    output.set(project.layout.buildDirectory.dir("outputs/renamed_apks/${variant.name}"))

                    // provide an instance of the artifact loader. This is necessary for
                    // some artifacts. See Artifact.ContainsMany
                    builtArtifactsLoader.set(variant.artifacts.getBuiltArtifactsLoader())
                }

                // Wire the task to respond to artifact creation
                variant.artifacts.use(copyTask).wiredWith {
                    it.input
                }.toListenTo(SingleArtifact.APK)


                // -- Verification --
                // the following is just to validate the recipe and is not actually
                // part of the recipe itself
                project.tasks.register<TemplateTask>("validate${variant.name.capitalized()}") {
                    // The input of the validation task should be the output of the copy task.
                    // The normal way to do this would be:
                    //     input.set(copyTask.flatMap { it.output }
                    // However, doing this will force running the task when we want it to run
                    // automatically when the normal APK packaging task run.
                    // So we set the input manually, and the validation task will have to be called
                    // separately (in a separate Gradle execution or Gradle will detect the
                    // lack of dependency between the 2 tasks and complain.
                    input.set(project.layout.buildDirectory.dir("outputs/renamed_apks/${variant.name}"))
                    variantName.set(variant.name)
                }
            }
        }
    }
}

/**
 * Validation task to verify the behavior of the recipe
 */
abstract class TemplateTask : DefaultTask() {
    @get:InputDirectory
    abstract val input: DirectoryProperty

    @get:Input
    abstract val variantName: Property<String>

    @TaskAction
    fun taskAction() {
        // manually look for the content of the folder
        checkFile("${variantName.get()}-Feb2024-12.apk")
        checkFile("output-metadata.json")
    }

    private fun checkFile(name: String) {
        val file = input.get().file(name).asFile
        if (file.isFile.not()) {
            throw RuntimeException("Expected file missing: $file")
        }
   }
}