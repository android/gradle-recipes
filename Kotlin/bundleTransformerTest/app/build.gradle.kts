plugins {
        id("com.android.application")
        kotlin("android")
        kotlin("android.extensions")
}
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import com.android.build.api.artifact.ArtifactType
import org.gradle.api.tasks.OutputFile
import com.android.utils.appendCapitalized

abstract class UpdateBundleFileTask: DefaultTask() {
    @get: InputFiles
    abstract val  initialBundleFile: RegularFileProperty

    @get: OutputFile
    abstract val updatedBundleFile: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val versionCode = "versionCode = 4"
        println("bundleFilePresent = " + initialBundleFile.isPresent)
        updatedBundleFile.get().asFile.writeText(versionCode)
    }
}
abstract class ConsumeBundleFileTask: DefaultTask() {
    @get: InputFiles
    abstract val finalBundle: RegularFileProperty
    
    @TaskAction
    fun taskAction() {
        println(finalBundle.get().asFile.readText())
    }
}
android {
    
        compileSdkVersion(29)
        defaultConfig {
            minSdkVersion(21)
            targetSdkVersion(29)
        }
    defaultConfig {
        versionCode = 3
    }

    onVariantProperties {
        val updateBundle = project.tasks.register<UpdateBundleFileTask>("${name}UpdateBundleFile") {
            initialBundleFile.set(artifacts.get(ArtifactType.BUNDLE))
        }
        val finalBundle = project.tasks.register<ConsumeBundleFileTask>("${name}ConsumeBundleFile") {
            finalBundle.set(artifacts.get(ArtifactType.BUNDLE))
        }
        artifacts.use(updateBundle)
            .wiredWithFiles(
                UpdateBundleFileTask::initialBundleFile,
                UpdateBundleFileTask::updatedBundleFile)
            .toTransform(ArtifactType.BUNDLE)
    }
}