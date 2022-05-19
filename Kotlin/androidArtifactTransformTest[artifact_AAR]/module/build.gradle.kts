plugins {
        id("com.android.library")
        kotlin("android")
}
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import com.android.build.api.artifact.SingleArtifact
import org.gradle.api.tasks.OutputFile
import com.android.utils.appendCapitalized

abstract class UpdateArtifactTask: DefaultTask() {
    @get: InputFiles
    abstract val  initialArtifact: RegularFileProperty

    @get: OutputFile
    abstract val updatedArtifact: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val versionCode = "artifactTransformed = true"
        println("artifactPresent = " + initialArtifact.isPresent)
        println("initialArtifact = " + initialArtifact.get().asFile)
        println("updatedArtifact = " + updatedArtifact.get().asFile)
        updatedArtifact.get().asFile.writeText(versionCode)
    }
}
abstract class ConsumeArtifactTask: DefaultTask() {
    @get: InputFiles
    abstract val finalArtifact: RegularFileProperty

    @TaskAction
    fun taskAction() {
        println(finalArtifact.get().asFile.readText())
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
    onVariants {
        val updateArtifact = project.tasks.register<UpdateArtifactTask>("${it.name}UpdateArtifact")
        val finalArtifact = project.tasks.register<ConsumeArtifactTask>("${it.name}ConsumeArtifact") {
            finalArtifact.set(it.artifacts.get(SingleArtifact.AAR))
        }
        it.artifacts.use(updateArtifact)
            .wiredWithFiles(
                UpdateArtifactTask::initialArtifact,
                UpdateArtifactTask::updatedArtifact)
        .toTransform(SingleArtifact.AAR)
    }
}