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
import org.gradle.api.Plugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.register

/**
 * This [Settings] plugin is applied to the settings script.
 * The settings script contains the list of modules applied to the project
 * which allows us to hook up on sub-project's creation.
 */
class CustomSettings: Plugin<Settings> {

    override fun apply(settings: Settings) {
        var taskProvider: TaskProvider<AllProjectsApkTask>? = null

        settings.gradle.beforeProject { project ->
            // since we want a single task that will consume all APKs produced by a
            // sub-project's RELEASE variant, let's create it in the rootProject only.
            if (project == project.rootProject) {
                taskProvider = project.tasks.register<AllProjectsApkTask>(
                    "allProjectsAction"
                ) {
                    // set the Task output file inside the build's output folder
                    outputFile.set(
                        project.layout.buildDirectory.file("list_of_apks.txt")
                    )
                }
            }

            // Registers a callback on the application of the Android Application plugin.
            // This allows the CustomPlugin to work whether it's applied before or after
            // the Android Application plugin.
            project.plugins.withType(AppPlugin::class.java) { _ ->

                // so we now know that the application plugin has been applied so
                // let's look up its extension so we can invoke the AGP Variant API.
                val androidComponents =
                    project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)

                // Registers a callback to be called, when a RELEASE variant is configured
                androidComponents.onVariants(
                    androidComponents.selector().withBuildType("release")
                ) { variant ->

                    // for each RELEASE variant, let's configure the unique task we created
                    // in the root project and add the APK artifact to its input file.
                    taskProvider?.configure { task ->
                        println("Adding variant ${variant.name} APKs from ${project.path}")
                        // look up the APK directory artifact as a Provider<Directory> and
                        // add it to the task's inputDirectories FileCollection.
                        task.inputDirectories.add(
                            variant.artifacts.get(SingleArtifact.APK)
                        )
                    }
                }
            }

        }
    }

}