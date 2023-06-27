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

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.BuildConfigField
import com.android.build.api.variant.Variant
import com.android.build.gradle.AppPlugin
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

/**
 * This custom plugin will add 2 build configs fields to all variants.
 * One field value come from a function, the other come from a task output.
 */
class CustomPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        // Since the input of this Task are not dependent on any Variant related
        // information, we create it as a singleton task instead of per-variant.
        // Each variant will use its output.
        val gitVersionProvider = project.tasks.register<GitVersionTask>("gitVersionProvider") {
            gitVersionOutputFile.set(
                File(project.buildDir, "intermediates/gitVersionProvider/output")
            )
            outputs.upToDateWhen { false }
        }

        // Registers a callback on the application of the Android Application plugin.
        // This allows the CustomPlugin to work whether it's applied before or after
        // the Android Application plugin.
        project.plugins.withType(AppPlugin::class.java) {

            // Queries for the extension set by the Android Application plugin.
            // This is the second of two entry points into the Android Gradle plugin
            val androidComponents =
                project.extensions.getByType(AndroidComponentsExtension::class.java)
            // Registers a callback to be called, when a new variant is configured
            androidComponents.onVariants { variant: Variant ->

                variant.buildConfigFields.put("FloatValue",
                    BuildConfigField(
                        type = "Float",
                        value = "${calculateFloatValue()}f",
                        comment = "Float Value")
                )

                variant.buildConfigFields.put("GitVersion", gitVersionProvider.map {  task ->
                    BuildConfigField(
                        type = "String",
                        value = "\"${task.gitVersionOutputFile.get().asFile.readText(Charsets.UTF_8)}\"",
                        comment = "Git Version")
                })
            }
        }
    }

    private fun calculateFloatValue(): Float {
        // here, the calculated value is rather simple.
        return 1f
    }

    /**
     * Task to obtain the git sha of the TOT.
     */
    abstract class GitVersionTask: DefaultTask() {

        @get:OutputFile
        abstract val gitVersionOutputFile: RegularFileProperty

        @TaskAction
        fun taskAction() {
            // this would be the code to get the tip of tree version,
            // val firstProcess = ProcessBuilder("git","rev-parse --short HEAD").start()
            // val error = firstProcess.errorStream.readBytes().decodeToString()
            // if (error.isNotBlank()) {
            //      System.err.println("Git error : $error")
            // }
            // var gitVersion = firstProcess.inputStream.readBytes().decodeToString()

            // but here, we are just hardcoding :
            gitVersionOutputFile.get().asFile.let { outputFile ->
                outputFile.parentFile.mkdirs()
                outputFile.writeText("1234")
            }
        }
    }
}
