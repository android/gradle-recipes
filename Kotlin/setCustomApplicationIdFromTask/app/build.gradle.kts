plugins {
        id("com.android.application")
}

android {
    compileSdkVersion(29)
defaultConfig {
    minSdkVersion(21)
    targetSdkVersion(29)
}
}

abstract class ApplicationIdProducerTask: DefaultTask() {

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun taskAction() {
        outputFile.get().asFile.writeText("set.from.task." + name)
    }
}

androidComponents {
    onVariants(selector().withBuildType("debug")) { variant ->
        val appIdProducer = tasks.register<ApplicationIdProducerTask>("${variant.name}AppIdProducerTask") {
            File(getBuildDir(), name).also {
                outputFile.set(File(it, "appId.txt"))
            }

        }
        variant.applicationId.set(appIdProducer.flatMap { task ->
                task.outputFile.map { it.asFile.readText() }
        })
    }
}