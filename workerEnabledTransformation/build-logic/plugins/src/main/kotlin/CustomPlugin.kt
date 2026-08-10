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

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import com.android.build.api.artifact.SingleArtifact

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

                // registering copy<Variant>Apk task and getting provider for it
                val copyApksProvider = project.tasks.register("copy${variant.name}Apks", CopyApksTask::class.java)

                // Adds the task as APK transformer. This automatically creates a dependency of `copyApks` task to the
                // last transformer of SingleArtifact.APK. This also creates transformationRequest of type
                // [com.android.build.api.artifact.ArtifactTransformationRequest]. It allows to submit WorkAction to
                // Gradle's [WorkQueue] to parallelize the transformations.
                // Lastly, this uses the toTransformMany() API, used on directory artifacts of type 'Single',
                // 'Transformable', and 'ContainsMany'.
                val transformationRequest = variant.artifacts.use(copyApksProvider)
                    .wiredWithDirectories(
                        CopyApksTask::apkFolder,
                        CopyApksTask::outFolder)
                    .toTransformMany(SingleArtifact.APK)

                // Configures copyApk task by adding transformation request as a property value
                copyApksProvider.configure {
                    it.transformationRequest.set(transformationRequest)
                }
            }
        }
    }
}