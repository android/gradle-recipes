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

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

/**
 * This custom plugin creates a task per variant that consumes the variant's bundle (.aab) output
 * file.
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
                val taskName = "check${variant.name}Bundle"
                // Registers a new task to verify the app bundle.
                project.tasks.register<CheckBundleTask>(taskName) {
                    // Sets this task's input to the SingleArtifact.BUNDLE artifact. This
                    // automatically creates a dependency between this new task and the task
                    // generating the app bundle.
                    bundle.set(variant.artifacts.get(SingleArtifact.BUNDLE))
                    output.set(
                        project.layout.buildDirectory.dir("intermediates/$taskName")
                    )
                }
            }
        }
    }
}