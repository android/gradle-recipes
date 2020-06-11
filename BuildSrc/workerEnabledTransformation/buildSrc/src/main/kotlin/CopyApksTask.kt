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
import com.android.build.api.artifact.ArtifactType
import com.android.build.api.artifact.ArtifactKind
import com.android.build.api.artifact.Artifact
import com.android.build.api.artifact.Artifact.Replaceable
import com.android.build.api.artifact.Artifact.ContainsMany
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
