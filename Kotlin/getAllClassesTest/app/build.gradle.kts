plugins {
        id("com.android.application")
        kotlin("android")
}
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.api.artifact.ScopedArtifact

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

abstract class GetAllClassesTask: DefaultTask() {

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @TaskAction
    fun taskAction() {

        allDirectories.get().forEach { directory ->
            println("Directory : ${directory.asFile.absolutePath}")
            directory.asFile.walk().filter(File::isFile).forEach { file ->
                println("File : ${file.absolutePath}")
            }
        }
        allJars.get().forEach { file ->
            println("JarFile : ${file.asFile.absolutePath}")
        }
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
        val taskProvider = project.tasks.register<GetAllClassesTask>("${variant.name}GetAllClasses")
        variant.artifacts.forScope(ScopedArtifacts.Scope.PROJECT)
            .use(taskProvider)
            .toGet(
                ScopedArtifact.CLASSES,
                GetAllClassesTask::allJars,
                GetAllClassesTask::allDirectories
            )
    }
}