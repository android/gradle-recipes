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
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.lang.RuntimeException
import java.util.zip.ZipFile

/**
 * This task checks that a variant's app bundle (.aab) output file contains the expected native
 * debug metadata.
 */
abstract class CheckBundleTask : DefaultTask() {

    // In order for the task to be up-to-date when the inputs have not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @get:InputFile
    abstract val bundle: RegularFileProperty

    @TaskAction
    fun taskAction() {
        ZipFile(bundle.get().asFile).use {
            val entries = it.entries().asSequence().toList()
            if (entries.count { it.name.endsWith("extra.so.dbg") } != 4) {
                throw RuntimeException("Expected bundle file to have exactly 4 extra.so.dbg entries.")
            }
        }
    }
}