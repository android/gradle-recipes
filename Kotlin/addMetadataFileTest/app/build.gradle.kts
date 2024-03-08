plugins {
        id("com.android.application")
        kotlin("android")
}
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import com.android.build.api.variant.BuiltArtifactsLoader
import com.android.build.api.artifact.SingleArtifact
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import java.util.zip.ZipFile

abstract class AddMetadataInBundleTask: DefaultTask() {
    @get:OutputFile
    abstract val metadataFile: RegularFileProperty

    @TaskAction
    fun taskAction() {
        metadataFile.get().asFile.writeText("some metadata")
    }
}

abstract class DisplayBundleTask: DefaultTask() {

    @get:InputFile
    abstract val bundle: RegularFileProperty

    @TaskAction
    fun taskAction() {

        ZipFile(bundle.get().asFile).use {
            it.entries().asIterator().forEach { entry ->
                println(entry.name)
            }
        }
    }
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        versionCode = 1
    }
}

androidComponents {
    onVariants { variant ->
        val metadataTask = project.tasks.register<AddMetadataInBundleTask>("${variant.name}AddMetadata") {
            File(getBuildDir(), name).also {
                    metadataFile.set(File(it, "metadata.pb"))
            }
        }

        variant.bundleConfig.addMetadataFile(
            "com.android.build",
            metadataTask.flatMap { it.metadataFile }
        )

        project.tasks.register<DisplayBundleTask>("${variant.name}DisplayBundle") {
            bundle.set(variant.artifacts.get(SingleArtifact.BUNDLE))
        }
    }
}