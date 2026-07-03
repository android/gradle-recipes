/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import java.io.File

/**
 * This is the pre-build task that runs before the build executes.
 * The task does a trivial validation of the Gradle version, and produces an output for verification in another task.
 */
abstract class PreBuildValidationTask : DefaultTask() {

    // In order for the task to be up-to-date when the inputs have not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @get:Input
    abstract val gradleVersion: Property<String>

    @TaskAction
    fun taskAction() {
        // This is a trivial validation, but a more useful one could be used in this task
        if (gradleVersion.get().isEmpty()) {
            throw RuntimeException("The Gradle version must not be empty.")
        }

        // The next part of this task is done only for verification purposes
        // delete the previous content. This task does not support incremental mode but could be modified to do so
        val outputFile = output.get().asFile
        outputFile.deleteRecursively()
        outputFile.mkdirs()

        val newFile = File(outputFile, "gradle_version.txt")
        newFile.writeText(gradleVersion.get())
    }
}
