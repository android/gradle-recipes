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
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.register

/**
 * This [Settings] plugin is applied to the settings script.
 *
 * INSERT DESCRIPTION
 */
class CustomSettings: Plugin<Settings> {
    override fun apply(settings: Settings) {

        settings.gradle.beforeProject { project ->

            // Registers a callback on the application of the Android Application plugin.
            // This allows the CustomPlugin to work whether it's applied before or after
            // the Android Application plugin.
            project.plugins.withType(AppPlugin::class.java) { _ ->

                // Look up the right component.
                val androidComponents =
                    project.extensions.getByType(AndroidComponentsExtension::class.java)

                // Registers a callback to be called, when a variant is configured
                androidComponents.onVariants { variant ->
                }
            }
        }
    }
}