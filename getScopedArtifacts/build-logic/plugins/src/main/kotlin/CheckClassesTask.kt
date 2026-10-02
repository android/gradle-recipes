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

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.lang.RuntimeException

/**
 * This task does a trivial check of a variant's classes.
 */
abstract class CheckClassesTask: DefaultTask() {

    // In order for the task to be up-to-date when the inputs have not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    /**
     * Project scope, not including dependencies.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val projectDirectories: ListProperty<Directory>

    /**
     * Project scope, not including dependencies.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val projectJars: ListProperty<RegularFile>

    /**
     * Full scope, including project scope and all dependencies.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val allDirectories: ListProperty<Directory>

    /**
     * Full scope, including project scope and all dependencies.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val allJars: ListProperty<RegularFile>

    /**
     * This task does a trivial check of the classes, but a similar task could be
     * written to perform useful verification.
     */
    @TaskAction
    fun taskAction() {

        // Check projectDirectories
        if (projectDirectories.get().isEmpty()) {
            throw RuntimeException("Expected projectDirectories not to be empty")
        }
        projectDirectories.get().firstOrNull()?.let {
            if (!it.asFile.walk().toList().any { file -> file.name == "MainActivity.class" }) {
                throw RuntimeException("Expected MainActivity.class in projectDirectories")
            }
        }

        // Check projectJars. We expect projectJars to include the project's R.jar but not jars
        // from dependencies (e.g., the kotlin stdlib jar)
        val projectJarFileNames = projectJars.get().map { it.asFile.name }
        if (!projectJarFileNames.contains("R.jar")) {
            throw RuntimeException("Expected project jars to contain R.jar")
        }
        if (projectJarFileNames.any { it.startsWith("kotlin-stdlib") }) {
            throw RuntimeException("Did not expect projectJars to contain kotlin stdlib")
        }

        // Check allDirectories
        if (allDirectories.get().isEmpty()) {
            throw RuntimeException("Expected allDirectories not to be empty")
        }
        allDirectories.get().firstOrNull()?.let {
            if (!it.asFile.walk().toList().any { file -> file.name == "MainActivity.class" }) {
                throw RuntimeException("Expected MainActivity.class in allDirectories")
            }
        }

        // Check allJars. We expect allJars to include jars from the project *and* its dependencies
        // (e.g., the kotlin stdlib jar).
        val allJarFileNames = allJars.get().map { it.asFile.name }
        if (!allJarFileNames.contains("R.jar")) {
            throw RuntimeException("Expected allJars to contain R.jar")
        }
        if (!allJarFileNames.any { it.startsWith("kotlin-stdlib") }) {
            throw RuntimeException("Expected allJars to contain kotlin stdlib")
        }
    }
}