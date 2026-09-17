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

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
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

            // Create the provider for the pre-build task
            val preBuildTaskProvider = project.tasks.register<PreBuildValidationTask>("preBuildValidation") {
                gradleVersion.set(project.gradle.gradleVersion)
                output.set(project.layout.buildDirectory.dir("preBuildOutput"))
            }

            val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

            // Registers a callback to be called, when a new variant is configured
            androidComponents.onVariants { variant ->
                // Register the pre-build task with the LifecycleTask API
                variant.lifecycleTasks.registerPreBuild(preBuildTaskProvider)

                // -- Verification --
                // the following is just to validate the recipe and is not actually part of the recipe itself
                project.tasks.register<ValidateTask>("validate${variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}") {
                    // The input of the validation task should be the output of the pre-build task.
                    // The normal way to do this would be:
                    //     input.set(preBuildTaskProvider.flatMap { it.output }
                    // However, doing this will force running the pre-build task when we want it to run
                    // automatically when build is invoked.
                    // So we set the input manually, and the validation task will have to be called
                    // separately (in a separate Gradle execution or Gradle will detect the
                    // lack of dependency between the 2 tasks and complain).
                    input.set(project.layout.buildDirectory.dir("preBuildOutput"))
                }
            }
        }
    }
}

/**
 * Validation task to verify the behavior of the recipe
 */
abstract class ValidateTask : DefaultTask() {

    @get:InputDirectory
    abstract val input: DirectoryProperty

    @TaskAction
    fun taskAction() {
        val gradleVersionFile = input.get().file("gradle_version.txt").asFile
        if (!gradleVersionFile.exists()) {
            throw RuntimeException("Expected file missing: $gradleVersionFile")
        }
    }
}
