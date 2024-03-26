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
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.lang.RuntimeException
import java.io.File

abstract class CheckNativeDebugMetadataTask : DefaultTask() {

    // In order for the task to be up-to-date when the inputs have not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @get:InputFiles
    abstract val nativeDebugMetadataDirs: ListProperty<Directory>

    @TaskAction
    fun taskAction() {
        // Validate the expected files are present in the transformed directory
        val transformedDir = nativeDebugMetadataDirs.get().first()
        val testFile0 = File(transformedDir.asFile, "testFile0.dbg")
        val testFile1 = File(transformedDir.asFile, "testFile1.dbg")
        if (!testFile0.exists() || !testFile1.exists()) {
            throw RuntimeException(
                "The expected test files are not present in the transformed directory.")
        }
        val testFile0ContentsExpected = testFile0.readText() == "test data 0"
        val testFile1ContentsExpected = testFile1.readText() == "test data 1"
        if (!testFile0ContentsExpected || !testFile1ContentsExpected) {
            throw RuntimeException(
                "The contents of the test files in the transformed directory are not expected.")
        }
    }
}
