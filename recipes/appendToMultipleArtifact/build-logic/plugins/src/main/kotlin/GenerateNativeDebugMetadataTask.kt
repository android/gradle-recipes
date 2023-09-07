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
import java.io.File
import java.lang.RuntimeException
import java.util.zip.ZipFile

/**
 * This task generates (fake) native debug metadata.
 */
abstract class GenerateNativeDebugMetadataTask : DefaultTask() {

    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @TaskAction
    fun taskAction() {
        val outputDir = output.get().asFile
        for (abi in listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")) {
            val abiDir = File(outputDir, abi).also { it.mkdirs() }
            File(abiDir, "extra.so.dbg").writeText("fake native debug metadata")
        }
    }
}