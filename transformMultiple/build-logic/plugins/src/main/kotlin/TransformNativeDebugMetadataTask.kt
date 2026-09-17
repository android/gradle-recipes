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
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.OutputDirectory
import java.io.File

/**
 * This is an example of a task that takes a collection of input directories and combines their contents into a single
 * output directory.
 */
abstract class TransformNativeDebugMetadataTask: DefaultTask() {

    @get:InputFiles
    abstract val inputDirectories: ListProperty<Directory>
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun taskAction() {
        // Combine the data from the input directories into the output directory
        inputDirectories.get().forEach { inputDir ->
            inputDir.asFile.copyRecursively(outputDir.get().asFile, overwrite = true)
        }
    }
}