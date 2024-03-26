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
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.ScopedArtifacts
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.register
import java.nio.file.Files

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
                // Call the transformClassesWith API: supply the class visitor factory, and specify the scope and
                // parameters
                variant.instrumentation.transformClassesWith(
                    ExampleClassVisitorFactory::class.java,
                    InstrumentationScope.PROJECT
                ) { params ->
                    params.newMethodName.set("transformedMethod")
                }

                // -- Verification --
                // the following is just to validate the recipe and is not actually part of the recipe itself
                val taskName = "check${variant.name.capitalized()}AsmTransformation"
                val taskProvider = project.tasks.register<CheckAsmTransformationTask>(taskName) {
                    output.set(
                        project.layout.buildDirectory.dir("intermediates/$taskName")
                    )
                }

                // This creates a dependency on the classes in the project scope, which will run the
                // necessary tasks to build the classes artifact and trigger the transformation
                variant.artifacts
                    .forScope(ScopedArtifacts.Scope.PROJECT)
                    .use(taskProvider)
                    .toGet(
                        ScopedArtifact.CLASSES,
                        CheckAsmTransformationTask::projectJars,
                        CheckAsmTransformationTask::projectDirectories,
                    )
            }
        }
    }
}