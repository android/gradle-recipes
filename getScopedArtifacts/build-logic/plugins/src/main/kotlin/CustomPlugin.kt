/*
 * Copyright 2023 The Android Open Source Project
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

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

/**
 * This custom plugin creates a task per variant that checks the variant's classes
 */
class CustomPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        // Registers a callback on the application of the Android Application plugin.
        // This allows the CustomPlugin to work whether it's applied before or after
        // the Android Application plugin.
        project.plugins.withType(AppPlugin::class.java) {

            // Queries for the extension set by the Android Application plugin.
            val androidComponents =
                project.extensions.getByType(AndroidComponentsExtension::class.java)

            // Registers a callback to be called, when a new variant is configured
            androidComponents.onVariants { variant ->

                // Registers a new task to verify the app classes.
                val taskName = "check${variant.name}Classes"
                val taskProvider = project.tasks.register<CheckClassesTask>(taskName) {
                    output.set(
                        project.layout.buildDirectory.dir("intermediates/$taskName")
                    )
                }

                // Sets the task's projectJars and projectDirectories inputs to the
                // ScopeArtifacts.Scope.PROJECT ScopedArtifact.CLASSES artifacts. This
                // automatically creates a dependency between this task and any tasks
                // generating classes in the PROJECT scope.
                variant.artifacts
                    .forScope(ScopedArtifacts.Scope.PROJECT)
                    .use(taskProvider)
                    .toGet(
                        ScopedArtifact.CLASSES,
                        CheckClassesTask::projectJars,
                        CheckClassesTask::projectDirectories,
                    )

                // Sets this task's allJars and allDirectories inputs to the
                // ScopeArtifacts.Scope.ALL ScopedArtifact.CLASSES artifacts. This
                // automatically creates a dependency between this task and any tasks
                // generating classes.
                variant.artifacts
                    .forScope(ScopedArtifacts.Scope.ALL)
                    .use(taskProvider)
                    .toGet(
                        ScopedArtifact.CLASSES,
                        CheckClassesTask::allJars,
                        CheckClassesTask::allDirectories,
                    )
            }
        }
    }
}