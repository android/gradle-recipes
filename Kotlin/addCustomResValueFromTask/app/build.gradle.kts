import com.android.build.api.variant.ResValue
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

abstract class GitVersionTask : DefaultTask() {

    @get:OutputFile abstract val gitVersionOutputFile: RegularFileProperty

    @ExperimentalStdlibApi
    @TaskAction
    fun taskAction() {

        // this would be the code to get the tip of tree version,
        // val firstProcess = ProcessBuilder("git","rev-parse --short HEAD").start()
        // val error = firstProcess.errorStream.readBytes().decodeToString()
        // if (error.isNotBlank()) {
        //      System.err.println("Git error : $error")
        // }
        // var gitVersion = firstProcess.inputStream.readBytes().decodeToString()

        // but here, we are just hardcoding :
        gitVersionOutputFile.get().asFile.writeText("1234")
    }
}

val gitVersionProvider =
    tasks.register<GitVersionTask>("gitVersionProvider") {
        File(project.buildDir, "intermediates/gitVersionProvider/output").also {
            it.parentFile.mkdirs()
            gitVersionOutputFile.set(it)
        }
        outputs.upToDateWhen { false }
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
        variant.resValues.put(
            variant.makeResValueKey("string", "GitVersion"),
            gitVersionProvider.map { task ->
                ResValue(
                    task.gitVersionOutputFile.get().asFile.readText(Charsets.UTF_8), "git version")
            })
    }
}
