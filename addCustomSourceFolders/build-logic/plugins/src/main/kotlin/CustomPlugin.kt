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
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import java.io.File

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

                val addSourceTaskProvider =  project.tasks.register<AddCustomSources>("${variant.name}AddCustomSources") {
                    outputFolder.set(File(project.layout.buildDirectory.asFile.get(), "toml/gen"))
                }
                File(project.projectDir, "third_party/${variant.name}/toml").mkdirs()

                variant.sources.getByName("toml").also {
                    // adding custom folders (static and generated) to `toml` source type
                    it.addStaticSourceDirectory("third_party/${variant.name}/toml")
                    it.addGeneratedSourceDirectory(addSourceTaskProvider, AddCustomSources::outputFolder)
                }

                project.tasks.register<DisplayAllSources>("${variant.name}DisplayAllSources") {
                    //to print all directories that are part of `toml` source type
                    sourceFolders.set(variant.sources.getByName("toml").all)
                }
            }
        }
    }
}