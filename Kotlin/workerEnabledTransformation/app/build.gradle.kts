import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.BuiltArtifact
import java.io.Serializable
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

interface WorkItemParameters : WorkParameters, Serializable {
    val inputApkFile: RegularFileProperty
    val outputApkFile: RegularFileProperty
}

abstract class WorkItem @Inject constructor(private val workItemParameters: WorkItemParameters) :
    WorkAction<WorkItemParameters> {
    override fun execute() {
        workItemParameters.outputApkFile.get().asFile.delete()
        workItemParameters.inputApkFile.asFile
            .get()
            .copyTo(workItemParameters.outputApkFile.get().asFile)
    }
}

abstract class CopyApksTask @Inject constructor(private val workers: WorkerExecutor) :
    DefaultTask() {

    @get:InputFiles abstract val apkFolder: DirectoryProperty

    @get:OutputDirectory abstract val outFolder: DirectoryProperty

    @get:Internal
    abstract val transformationRequest: Property<ArtifactTransformationRequest<CopyApksTask>>

    @TaskAction
    fun taskAction() {

        transformationRequest.get().submit(this, workers.noIsolation(), WorkItem::class.java) {
            builtArtifact: BuiltArtifact,
            outputLocation: Directory,
            param: WorkItemParameters ->
            val inputFile = File(builtArtifact.outputFile)
            param.inputApkFile.set(inputFile)
            param.outputApkFile.set(File(outputLocation.asFile, inputFile.name))
            param.outputApkFile.get().asFile
        }
    }
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
    }
}

androidComponents {
    onVariants { variant ->
        val copyApksProvider = tasks.register<CopyApksTask>("copy${variant.name}Apks")

        val transformationRequest =
            variant.artifacts
                .use(copyApksProvider)
                .wiredWithDirectories(CopyApksTask::apkFolder, CopyApksTask::outFolder)
                .toTransformMany(SingleArtifact.APK)

        copyApksProvider.configure { this.transformationRequest.set(transformationRequest) }
    }
}
