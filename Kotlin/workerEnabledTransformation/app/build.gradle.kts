plugins {
        id("com.android.application")
        kotlin("android")
        kotlin("android.extensions")
}
import java.io.Serializable
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import com.android.build.api.artifact.ArtifactType
import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.variant.BuiltArtifact


interface WorkItemParameters: WorkParameters, Serializable {
    val inputApkFile: RegularFileProperty
    val outputApkFile: RegularFileProperty
}

abstract class WorkItem @Inject constructor(private val workItemParameters: WorkItemParameters)
    : WorkAction<WorkItemParameters> {
    override fun execute() {
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

      transformationRequest.get().submit(
         this, 
         workers.noIsolation(),
         WorkItem::class.java,
         WorkItemParameters::class.java) {
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

    onVariantProperties {
        val copyApksProvider = tasks.register<CopyApksTask>("copy${name}Apks")

        val transformationRequest = artifacts.use(copyApksProvider)
            .wiredWithDirectories(
                CopyApksTask::apkFolder,
                CopyApksTask::outFolder)
            .toTransformMany(ArtifactType.APK)

        copyApksProvider.configure {
            this.transformationRequest.set(transformationRequest)
            this.outFolder.set(File("/usr/local/google/home/jedo/src/studio-4.1-dev/out/apiTests/Kotlin/workerEnabledTransformation/build/acme_apks"))
        }
    }
}