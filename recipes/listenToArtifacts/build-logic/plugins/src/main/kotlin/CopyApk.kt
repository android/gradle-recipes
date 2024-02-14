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

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.BuiltArtifactsLoader
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.com.google.common.io.Files
import java.io.File

/**
 * This task will receive a folder of APK to copy and a folder to copy them to.
 *
 * This will also receive a [BuiltArtifactsLoader]. This is specific to some artifacts that
 * have metadata associated with them (in this case [SingleArtifact.APK]). This will allow to
 * find all the apks and load their metadata, instead of just going through the folder.
 */
abstract class CopyApk : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val input: DirectoryProperty

    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @get:Internal
    abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

    @TaskAction
    fun taskAction() {
        // delete the previous content. This task does not support incremental mode but could
        // be modified to do so
        val outputDirectory = output.get()
        val outputFile = outputDirectory.asFile

        outputFile.deleteRecursively()
        outputFile.mkdirs()

        // this will load the content of the folder and give access to all items representing
        // the artifact with their metadata
        val builtArtifacts = builtArtifactsLoader.get().load(input.get())
            ?: throw RuntimeException("Cannot load APKs")

        builtArtifacts.elements.forEach { artifact ->
            // construct a new name to copy the APK, using some of the APK metadata
            val name = buildString {
                append(builtArtifacts.variantName)
                artifact.versionName?.let {
                    if (it.isNotBlank()) {
                        append("-$it")
                    }
                }
                artifact.versionCode?.let {
                    append("-$it")
                }
                append(".apk")
            }

            Files.copy(File(artifact.outputFile), outputDirectory.file(name).asFile)
        }

        // The above will only save the artifact themselves. It will not save the
        // metadata associated with them. Depending on our needs we may need to copy it.
        // This is required when transforming such an artifact. We'll do it here for demonstration
        // purpose.
        builtArtifacts.save(outputDirectory)
    }
}