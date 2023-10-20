/*
 * Copyright 2022 The Android Open Source Project
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
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
abstract class AddCustomSources: DefaultTask() {

    @get:OutputDirectory
    abstract val outputFolder: DirectoryProperty

    @TaskAction
    fun taskAction() {
        val outputFile = File(outputFolder.asFile.get(), "com/foo/bar.toml")
        outputFile.parentFile.mkdirs()
        // generate sample toml file
        outputFile.writeText("""
        [clients]
        data = [ ["gamma", "delta"], [1, 2] ]
        """.trimIndent())
    }
}