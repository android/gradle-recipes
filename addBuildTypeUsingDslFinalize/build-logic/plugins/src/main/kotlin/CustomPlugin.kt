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

/**
 * This custom plugin will create a new build type called 'extra'.
 * A [VerifierTask] will be created for each variant so we can verify that one
 * is created for the newly added build type (task should be called extraVerifyRecipe).
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

            // Invoke the dsl finalization block :
            // This is called after the user's build file has executed
            // and it is therefore the right time to make final changes to the
            // build configuration. In this example, we add a new build type.
            androidComponents.finalizeDsl { extension ->
                // look up `extra` or create one if it was not created by the user.
                val buildType = extension.buildTypes.maybeCreate("extra")
                buildType.isJniDebuggable = true
            }

            // Registers a callback to be called, when a new variant is configured.
            // It should be called with variants created with the `extra` build type.
            androidComponents.onVariants { variant ->
                project.tasks.register<VerifierTask>("${variant.name}VerifyRecipe") {
                    variantName.set(variant.name)
                    output.set(
                        project.layout.buildDirectory.dir("intermediates/recipe/$it.name")
                    )
                }
            }
        }
    }
}

/**
 * This task here to verify that the API does what is says.
 */
abstract class VerifierTask : DefaultTask() {

    // In order of the task to be up-to-date when the inputs have not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed or not
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @get:Input
    abstract val variantName: Property<String>

    @TaskAction
    fun taskAction() {
        if (variantName.get().equals("extra")) {
            println("Success : `extra` BuildType successfully created")
        }
    }
}