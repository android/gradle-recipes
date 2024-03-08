plugins {
        id("com.android.application")
        kotlin("android")
        kotlin("android.extensions")
}
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction


abstract class DisplayAllSources: DefaultTask() {

    @get:InputFiles
    abstract val sourceFolders: ListProperty<Directory>

    @TaskAction
    fun taskAction() {

        sourceFolders.get().forEach { directory ->
            println(">>> Got a Directory $directory")
            println("<<<")
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
        variant.sources.java.addSrcDir("custom/src/java/${variant.name}")

        project.tasks.register<DisplayAllSources>("${variant.name}DisplayAllSources") {
            sourceFolders.set(variant.sources.java.all)
        }
    }
}