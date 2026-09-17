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

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * This custom plugin creates tasks that append to ScopedArtifact and verifies it.
 */
class CustomPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        // Registers a callback on the application of the Android Application plugin.
        // This allows the CustomPlugin to work whether it's applied before or after
        // the Android Application plugin.
        project.plugins.withType(AppPlugin::class.java) {

            // Queries for the extension set by the Android Application plugin.
            val androidComponents =
                project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)

            // Registers a callback to be called, when a new variant is configured
            androidComponents.onVariants { variant ->

                val addDirectoryClassTaskProvider =
                    project.tasks.register("add${variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}DirectoryClass", AddDirectoryClassTask::class.java)
                val addJarClassTaskProvider =
                    project.tasks.register("add${variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}JarClass", AddJarClassTask::class.java)
                // Append the directory to the PROJECT scope classes
                variant.artifacts
                    .forScope(ScopedArtifacts.Scope.PROJECT)
                    .use(addDirectoryClassTaskProvider)
                    .toAppend(
                        ScopedArtifact.CLASSES,
                        AddDirectoryClassTask::outputDirectory
                    )
                // Append the jar file to the ALL scope classes
                variant.artifacts
                    .forScope(ScopedArtifacts.Scope.ALL)
                    .use(addJarClassTaskProvider)
                    .toAppend(
                        ScopedArtifact.CLASSES,
                        AddJarClassTask::outputJar
                    )

                // -- Verification --
                // the following is just to validate the recipe and is not actually part of the recipe itself
                val taskName = "check${variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}Classes"
                val checkClassesTaskProvider = project.tasks.register<CheckClassesTask>(taskName) {
                    output.set(
                        project.layout.buildDirectory.dir("intermediates/$taskName")
                    )
                }

                // Sets the task's projectJars and projectDirectories inputs to the
                // ScopeArtifacts.Scope.PROJECT ScopedArtifact.CLASSES artifacts. This automatically creates a
                // dependency between this task and any tasks generating classes in the PROJECT scope.
                variant.artifacts
                    .forScope(ScopedArtifacts.Scope.PROJECT)
                    .use(checkClassesTaskProvider)
                    .toGet(
                        ScopedArtifact.CLASSES,
                        CheckClassesTask::projectJars,
                        CheckClassesTask::projectDirectories,
                    )
                // Sets this task's allJars and allDirectories inputs to the ScopeArtifacts.Scope.ALL
                // ScopedArtifact.CLASSES artifacts. This automatically creates a dependency between this task
                // and any tasks generating classes.
                variant.artifacts
                    .forScope(ScopedArtifacts.Scope.ALL)
                    .use(checkClassesTaskProvider)
                    .toGet(
                        ScopedArtifact.CLASSES,
                        CheckClassesTask::allJars,
                        CheckClassesTask::allDirectories,
                    )
            }
        }
    }
}

/**
 * This task appends a directory containing a class file to the ScopedArtifact.
 */
abstract class AddDirectoryClassTask: DefaultTask() {

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun taskAction() {
        // Adds a new file to the directory that is being added to the ScopedArtifact.CLASSES
        File(outputDirectory.get().asFile, "New.class").createNewFile()
    }
}

/**
 * This task appends a jar file to the ScopedArtifact.
 */
abstract class AddJarClassTask: DefaultTask() {

    @get:OutputFile
    abstract val outputJar: RegularFileProperty

    @TaskAction
    fun taskAction() {
        // Create the jar file being added to the ScopedArtifact.CLASSES
        outputJar.get().asFile.createNewFile()
    }
}
