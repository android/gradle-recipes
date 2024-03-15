plugins {
        id("com.android.application")
        kotlin("android")
}
import java.io.Serializable
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.variant.BuiltArtifact


interface WorkItemParameters: WorkParameters, Serializable {
    val inputApkFile: RegularFileProperty
    val outputApkFile: RegularFileProperty
}

abstract class WorkItem @Inject constructor(private val workItemParameters: WorkItemParameters)
    : WorkAction<WorkItemParameters> {
    override fun execute() {
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

    }
}


android {
    namespace = "com.android.build.example.minimal"
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
    }
}
androidComponents {
    onVariants { variant ->
        val copyApksProvider = tasks.register<CopyApksTask>("copy${variant.name}Apks")

        val transformationRequest = variant.artifacts.use(copyApksProvider)
            .wiredWithDirectories(
                CopyApksTask::apkFolder,
                CopyApksTask::outFolder)
            .toTransformMany(SingleArtifact.APK)


        copyApksProvider.configure {
            this.transformationRequest.set(transformationRequest)
        }
    }
}