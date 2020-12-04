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


abstract class ManifestProducerTask: DefaultTask() {
    @get:InputFile
    abstract val gitInfoFile: RegularFileProperty

    @get:OutputFile
    abstract val outputManifest: RegularFileProperty

    @TaskAction
    fun taskAction() {

        val gitVersion = gitInfoFile.get().asFile.readText()
        val manifest = """<?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="com.android.build.example.minimal"
        android:versionName="${gitVersion}"
        android:versionCode="1" >
        <application android:label="Minimal">
            <activity android:name="MainActivity">
                <intent-filter>
                    <action android:name="android.intent.action.MAIN" />
                    <category android:name="android.intent.category.LAUNCHER" />
                </intent-filter>
            </activity>
        </application>
    </manifest>
        """
        println("Writes to " + outputManifest.get().asFile.absolutePath)
        outputManifest.get().asFile.writeText(manifest)
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
    val gitVersionProvider = tasks.register<GitVersionTask>("gitVersionProvider") {
        gitVersionOutputFile.set(
            File(project.buildDir, "intermediates/gitVersionProvider/output"))
        outputs.upToDateWhen { false }
    }
    onVariants { variant ->
        val manifestProducer = tasks.register<ManifestProducerTask>("${variant.name}ManifestProducer") {
            gitInfoFile.set(gitVersionProvider.flatMap(GitVersionTask::gitVersionOutputFile))
        }
        variant.artifacts.use(manifestProducer)
            .wiredWith(ManifestProducerTask::outputManifest)
            .toCreate(ArtifactType.MERGED_MANIFEST)
    }
}