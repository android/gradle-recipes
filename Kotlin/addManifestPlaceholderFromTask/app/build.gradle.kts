plugins {
        id("com.android.application")
        kotlin("android")
        kotlin("android.extensions")
}

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import com.android.build.api.artifact.ArtifactType


abstract class GitVersionTask: DefaultTask() {

    @get:OutputFile
    abstract val gitVersionOutputFile: RegularFileProperty

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


val gitVersionProvider = tasks.register<GitVersionTask>("gitVersionProvider") {
    File(project.buildDir, "intermediates/gitVersionProvider/output").also {
        it.parentFile.mkdirs()
        gitVersionOutputFile.set(it)
    }
    outputs.upToDateWhen { false }
}

abstract class ManifestReaderTask: DefaultTask() {

    @get:InputFile
    abstract val mergedManifest: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val manifest = mergedManifest.asFile.get().readText()
        // ensure that merged manifest contains the right activity name.
        if (!manifest.contains("activity android:name=\"com.android.build.example.minimal.NameWithGit-"))
            throw RuntimeException("Manifest Placeholder not replaced successfully")
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
    onVariants {
        val manifestReader = tasks.register<ManifestReaderTask>("${it.name}ManifestReader") {
            mergedManifest.set(it.artifacts.get(ArtifactType.MERGED_MANIFEST))
        }
        it.manifestPlaceholders.put("MyName", gitVersionProvider.map { task ->
            "NameWithGit-" + task.gitVersionOutputFile.get().asFile.readText(Charsets.UTF_8)
        })
    }
}