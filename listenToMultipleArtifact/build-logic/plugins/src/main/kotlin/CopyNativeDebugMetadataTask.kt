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

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * This task will receive the native debug metadata folder and copy the contents to the output
 */
abstract class CopyNativeDebugMetadataTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val debugMetadataDirectories: ListProperty<Directory>

    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @TaskAction
    fun taskAction() {
        // delete the previous content. This task does not support incremental mode but could be modified to do so
        val outputDirectory = output.get()
        val outputFile = outputDirectory.asFile

        outputFile.deleteRecursively()
        outputFile.mkdirs()

        val debugMetadataFile = File(debugMetadataDirectories.get().first().asFile, "test_file.dbg")
        debugMetadataFile.copyTo(File(outputFile, "renamed_test_file.dbg"))
    }
}
