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

import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.register
import com.android.build.api.artifact.SingleArtifact
import java.io.File
import com.android.build.api.variant.AndroidComponentsExtension

/**
 * This custom plugin will register tree tasks that will update and veridy Android manifest.
 */
class CustomPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Registers a callback on the application of the Android Application plugin.
        // This allows the CustomPlugin to work whether it's applied before or after
        // the Android Application plugin.
        project.plugins.withType(AppPlugin::class.java) {

            // register single task for getting versions from git
            val gitVersionProvider =
                project.tasks.register("gitVersionProvider", GitVersionTask::class.java) {
                    it.gitVersionOutputFile.set(
                        File(project.buildDir, "intermediates/gitVersionProvider/output")
                    )
                    it.outputs.upToDateWhen { false } // never use cache
                }

            val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
            // Registers a callback to be called, when a new variant is configured
            androidComponents.onVariants { variant ->

                val manifestUpdater =
                    project.tasks.register(variant.name + "ManifestUpdater", ManifestTransformerTask::class.java) {
                        it.gitInfoFile.set(gitVersionProvider.flatMap(GitVersionTask::gitVersionOutputFile))
                    }
                // update manifest with version information that we got from gitVersionProvider
                variant.artifacts.use(manifestUpdater)
                    .wiredWithFiles(
                        ManifestTransformerTask::mergedManifest,
                        ManifestTransformerTask::updatedManifest
                    ).toTransform(SingleArtifact.MERGED_MANIFEST)

                // -- Verification --
                // the following is just to validate the recipe and is not actually part of the recipe itself
                val taskName = "check${variant.name.capitalized()}MergedManifest"
                project.tasks.register<CheckMergedManifestTask>(taskName) {
                    mergedManifest.set(
                        variant.artifacts.get(SingleArtifact.MERGED_MANIFEST)
                    )
                    gitInfoFile.set(gitVersionProvider.flatMap(GitVersionTask::gitVersionOutputFile))
                    output.set(project.layout.buildDirectory.dir("intermediates/$taskName"))
                }
            }
        }
    }
}