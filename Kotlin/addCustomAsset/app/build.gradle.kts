plugins {
        id("com.android.application")
        kotlin("android")
        kotlin("android.extensions")
}

import com.android.build.api.artifact.MultipleArtifact

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
    }
}

abstract class AssetCreatorTask: DefaultTask() {
    @get:OutputFiles
    abstract val outputDirectory: DirectoryProperty

    @ExperimentalStdlibApi
    @TaskAction
    fun taskAction() {
        outputDirectory.get().asFile.mkdirs()
        File(outputDirectory.get().asFile, "custom_asset.txt")
            .writeText("some real asset file")
    }
}

androidComponents {
    onVariants(selector().withBuildType("debug")) { variant ->

        val assetCreationTask =
            project.tasks.register<AssetCreatorTask>("create${variant.name}Asset")
        variant.artifacts.use(assetCreationTask)
                .wiredWith(
                    AssetCreatorTask::outputDirectory
                )
                .toAppendTo(MultipleArtifact.ASSETS)
    }
}