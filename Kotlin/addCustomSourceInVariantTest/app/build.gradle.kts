plugins {
        id("com.android.application")
        kotlin("android")
}

abstract class AddCustomSources: DefaultTask() {

    @get:OutputDirectory
    abstract val outputFolder: DirectoryProperty

    @TaskAction
    fun taskAction() {
        val outputFile = File(outputFolder.asFile.get(), "com/foo/bar.toml")
        outputFile.parentFile.mkdirs()
        outputFile.writeText("""
            [clients]
            data = [ ["gamma", "delta"], [1, 2] ]
        """)
    }
}

abstract class DisplayAllSources: DefaultTask() {

    @get:InputFiles
    abstract val sourceFolders: ListProperty<Directory>

    @TaskAction
    fun taskAction() {

        sourceFolders.get().forEach { directory ->
            println("--> Got a Directory $directory")
            println("<-- done")
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
        val addSourceTaskProvider =  project.tasks.register<AddCustomSources>("${variant.name}AddCustomSources") {
            outputFolder.set(File(project.layout.buildDirectory.asFile.get(), "toml/gen"))
        }

        variant.sources.getByName("toml").also {
                it.addStaticSourceDirectory("src/${variant.name}/toml")
                it.addGeneratedSourceDirectory(addSourceTaskProvider, AddCustomSources::outputFolder)
        }
        println(variant.sources.getByName("toml"))

        project.tasks.register<DisplayAllSources>("${variant.name}DisplayAllSources") {
            sourceFolders.set(variant.sources.getByName("toml").all)
        }
    }
}