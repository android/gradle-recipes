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
import org.gradle.api.provider.Property
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import java.lang.RuntimeException

/**
 * This custom plugin shows examples for the onVariants API which includes updating the application ID.
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
                variant.applicationId.set("${variant.name}.applicationId")
                val taskName = "check${variant.name}ApplicationId"
                project.tasks.register<CheckApplicationIdTask>(taskName) {
                    output.set(
                        project.layout.buildDirectory.dir("$taskName")
                    )
                    variantName.set(variant.name)
                    applicationId.set(variant.applicationId)
                }
            }
        }
    }
}

/**
 * This task checks and outputs the name of the application ID.
 */
abstract class CheckApplicationIdTask : DefaultTask() {

    // In order of the task to be up-to-date when the inputs have not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @get:Input
    abstract val variantName: Property<String>

    @get:Input
    abstract val applicationId: Property<String>

    @TaskAction
    fun taskAction() {
        val expectedApplicationId = "${variantName.get()}.applicationId"
        if (applicationId.get() != expectedApplicationId) {
            throw RuntimeException("Expected application ID to be '$expectedApplicationId'")
        }
        println("Application ID for ${variantName.get()} variant: ${applicationId.get()}")
    }
}