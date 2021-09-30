plugins {
        id("com.android.application")
        kotlin("android")
        kotlin("android.extensions")
}
import com.android.build.api.artifact.MultipleArtifact

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

abstract class GetAllClassesTask: DefaultTask() {

    @get:InputFiles
    abstract val allClasses: ListProperty<Directory>

    @get:InputFiles
    abstract val allJarsWithClasses: ListProperty<RegularFile>

    @TaskAction
    fun taskAction() {

        allClasses.get().forEach { directory ->
            println("Directory : ${directory.asFile.absolutePath}")
            directory.asFile.walk().filter(File::isFile).forEach { file ->
                println("File : ${file.absolutePath}")
            }
            allJarsWithClasses.get().forEach { file ->
                println("JarFile : ${file.asFile.absolutePath}")
            }
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
        project.tasks.register<GetAllClassesTask>("${variant.name}GetAllClasses") {
            allClasses.set(variant.artifacts.getAll(MultipleArtifact.ALL_CLASSES_DIRS))
            allJarsWithClasses.set(variant.artifacts.getAll(MultipleArtifact.ALL_CLASSES_JARS))
        }
    }
}