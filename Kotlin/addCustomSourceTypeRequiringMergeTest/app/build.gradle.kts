plugins {
        id("com.android.application")
        kotlin("android")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
    }
}


abstract class MergeTomlSources: DefaultTask() {

    @get:InputFiles
    abstract val sourceFolders: ListProperty<Directory>

    @get:OutputDirectory
    abstract val mergedFolder: DirectoryProperty

    @TaskAction
    fun taskAction() {

        sourceFolders.get().forEach { directory ->
            println("--> Got a Directory $directory")
            directory.asFile.walk().forEach { sourceFile ->
                println("Source: " + sourceFile.absolutePath)
            }
            println("<-- done")
        }
    }
}

abstract class ConsumeMergedToml: DefaultTask() {

    @get:InputDirectory
    abstract val mergedFolder: DirectoryProperty

    @TaskAction
    fun taskAction() {

        println("Merged folder is " + mergedFolder.get().asFile)
    }
}


androidComponents {
    registerSourceType("toml")
    onVariants { variant ->

        val outFolder = project.layout.buildDirectory.dir("intermediates/${variant.name}/merged_toml")
        val mergingTask = project.tasks.register<MergeTomlSources>("${variant.name}MergeTomlSources") {
            sourceFolders.set(variant.sources.getByName("toml").all)
            mergedFolder.set(outFolder)
        }


        val consumingTask = project.tasks.register<ConsumeMergedToml>("${variant.name}ConsumeMergedToml") {
            mergedFolder.set(mergingTask.flatMap { it.mergedFolder })
        }
    }
}