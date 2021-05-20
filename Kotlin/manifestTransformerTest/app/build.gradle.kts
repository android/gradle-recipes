        plugins {
                id("com.android.application")
                kotlin("android")
                kotlin("android.extensions")
        }
        
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
        

        
        abstract class ManifestTransformerTask: DefaultTask() {

            @get:InputFile
            abstract val gitInfoFile: RegularFileProperty

            @get:InputFile
            abstract val mergedManifest: RegularFileProperty

            @get:OutputFile
            abstract val updatedManifest: RegularFileProperty

            @TaskAction
            fun taskAction() {

                val gitVersion = gitInfoFile.get().asFile.readText()
                var manifest = mergedManifest.asFile.get().readText()
                manifest = manifest.replace("android:versionCode=\"1\"", "android:versionCode=\"${gitVersion}\"")
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
        }
        androidComponents {
            onVariants { variant ->
                val gitVersionProvider = tasks.register<GitVersionTask>("${variant.name}GitVersionProvider") {
                    gitVersionOutputFile.set(
                        File(project.buildDir, "intermediates/gitVersionProvider/output"))
                    outputs.upToDateWhen { false }
                }

                val manifestUpdater = tasks.register<ManifestTransformerTask>("${variant.name}ManifestUpdater") {
                    gitInfoFile.set(gitVersionProvider.flatMap(GitVersionTask::gitVersionOutputFile))
                }
                variant.artifacts.use(manifestUpdater)
                    .wiredWithFiles(
                        ManifestTransformerTask::mergedManifest,
                        ManifestTransformerTask::updatedManifest)
                    .toTransform(com.android.build.api.artifact.SingleArtifact.MERGED_MANIFEST)
            }
        }