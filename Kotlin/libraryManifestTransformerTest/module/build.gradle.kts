plugins {
        id("com.android.library")
        kotlin("android")
        kotlin("android.extensions")
}


abstract class ManifestTransformerTask: DefaultTask() {

    @get:Input
    abstract val activityName: Property<String>

    @get:InputFile
    abstract val mergedManifest: RegularFileProperty

    @get:OutputFile
    abstract val updatedManifest: RegularFileProperty

    @TaskAction
    fun taskAction() {

        var manifest = mergedManifest.asFile.get().readText()
        manifest = manifest.replace("<application",
        "<uses-permission android:name=\"android.permission.INTERNET\"/>\n<application")
        println("Writes to " + updatedManifest.get().asFile.getAbsolutePath())
        updatedManifest.get().asFile.writeText(manifest)
    }
}

android {
    
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
    }

    onVariantProperties {
        val manifestUpdater = tasks.register<ManifestTransformerTask>("${name}ManifestUpdater") {
            activityName.set("ManuallyAdded")
        }
        artifacts.use(manifestUpdater)
            .wiredWithFiles(
                ManifestTransformerTask::mergedManifest,
                ManifestTransformerTask::updatedManifest)
            .toTransform(com.android.build.api.artifact.ArtifactType.MERGED_MANIFEST)  
    }
}