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

import java.io.Serializable
import java.io.File
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import org.gradle.workers.WorkAction
import com.android.build.api.artifact.Artifact
import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.variant.BuiltArtifact

interface WorkItemParameters: WorkParameters, Serializable {
    val inputApkFile: RegularFileProperty
    val outputApkFile: RegularFileProperty
}

// This is implementation of [WorkAction] that represents a unit of work.
// A work action should be an abstract class implementing the execute() method.
// In our example `WorkAction` just copies APK from `inputApkFile` to `outputApkFile` location.
// You can check [Gradle docs](https://docs.gradle.org/current/javadoc/org/gradle/workers/WorkAction.html)
// for more information.
abstract class WorkItem @Inject constructor(private val workItemParameters: WorkItemParameters)
    : WorkAction<WorkItemParameters> {
    override fun execute() {
        // business logic that gradle executes - copying artifact
        workItemParameters.outputApkFile.get().asFile.delete()
        workItemParameters.inputApkFile.asFile.get().copyTo(
            workItemParameters.outputApkFile.get().asFile)
    }
}

abstract class CopyApksTask @Inject constructor(private val workers: WorkerExecutor): DefaultTask() {

    @get:InputFiles
    abstract val apkFolder: DirectoryProperty

    @get:OutputDirectory
    abstract val outFolder: DirectoryProperty

    @get:Internal
    abstract val transformationRequest: Property<ArtifactTransformationRequest<CopyApksTask>>

    @TaskAction
    fun taskAction() {
        // Submits [WorkItem] to process each input of [BuiltArtifact].
        // `WorkItemParameters` serves as a configuration for `WorkItem`
        // and needs to be initialized. `workers` is injected as constructor parameter by
        // Gradle. We submit the [WorkItem] to the [transformationRequest] that
        // will manage the transformation registration and submission to the [workers] engine.
        transformationRequest.get().submit(
            this,
            workers.noIsolation(),
            WorkItem::class.java) {
                builtArtifact: BuiltArtifact,
                outputLocation: Directory,
                param: WorkItemParameters ->
            // This lambda expression configures instance of [WorkItemParameters] for [BuiltArtifact].
            // After this lambda `WorkItemParameters` has all indromation for `WorkItem`
            val inputFile = File(builtArtifact.outputFile)
            param.inputApkFile.set(inputFile)
            param.outputApkFile.set(File(outputLocation.asFile, inputFile.name))
            param.outputApkFile.get().asFile
        }
    }
}