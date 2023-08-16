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
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.lang.RuntimeException

/**
 * This task does a trivial check of a variant's multiDexKeepProguard files.
 */
abstract class CheckMultiDexKeepProguardTask : DefaultTask() {

    // In order for the task to be up-to-date when the inputs have not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @get:InputFiles
    abstract val multiDexKeepProguardFiles: ConfigurableFileCollection

    @TaskAction
    fun taskAction() {
        // This task does a trivial check of the multiDexKeepProguard files, but a similar task
        // could be written to perform useful verification of the files.
        val files = multiDexKeepProguardFiles.files.filter { it.name == "main_dex_rules.txt" }
        if (files.isEmpty()) {
            throw RuntimeException("Expected multiDexKeepProguard file main_dex_rules.txt")
        }
    }
}