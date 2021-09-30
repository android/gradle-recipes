plugins {
        id("com.android.application")
        kotlin("android")
        kotlin("android.extensions")
}
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import com.android.build.api.variant.BuiltArtifactsLoader
import com.android.build.api.artifact.SingleArtifact
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal

abstract class DisplayApksTask: DefaultTask() {

    @get:InputFiles
    abstract val apkFolder: DirectoryProperty

    @get:Internal
    abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

    @TaskAction
    fun taskAction() {

        val builtArtifacts = builtArtifactsLoader.get().load(apkFolder.get())
            ?: throw RuntimeException("Cannot load APKs")
        builtArtifacts.elements.forEach {
            println("Got an APK at ${it.outputFile}")
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
        project.tasks.register<DisplayApksTask>("${variant.name}DisplayApks") {
            apkFolder.set(variant.artifacts.get(SingleArtifact.APK))
            builtArtifactsLoader.set(variant.artifacts.getBuiltArtifactsLoader())
        }
    }
}