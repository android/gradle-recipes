plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.android.recipes.add_custom_asset"
    compileSdk = 29
    defaultConfig {
        minSdk = 21
    }
}

abstract class AssetCreatorTask : DefaultTask() {
    @get:OutputFiles
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun taskAction() {
        outputDirectory.get().asFile.mkdirs()
        File(outputDirectory.get().asFile, "custom_asset.txt").writeText("some real asset file")
    }
}

androidComponents {
    onVariants(selector().withBuildType("debug")) { variant ->
        val assetCreationTask =
            project.tasks.register<AssetCreatorTask>("create${variant.name}Asset")

        variant.sources.assets?.addGeneratedSourceDirectory(
            assetCreationTask, AssetCreatorTask::outputDirectory
        )
    }
}
