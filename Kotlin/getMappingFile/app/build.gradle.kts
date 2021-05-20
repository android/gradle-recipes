        plugins {
                id("com.android.application")
                kotlin("android")
                kotlin("android.extensions")
        }
        import org.gradle.api.DefaultTask
        import org.gradle.api.file.RegularFileProperty
        import org.gradle.api.tasks.InputFile
        import org.gradle.api.tasks.TaskAction

        import com.android.build.api.variant.BuiltArtifactsLoader
        import com.android.build.api.artifact.SingleArtifact
        import org.gradle.api.provider.Property
        import org.gradle.api.tasks.Internal

        abstract class MappingFileUploadTask: DefaultTask() {

            @get:InputFile
            abstract val mappingFile: RegularFileProperty

            @TaskAction
            fun taskAction() {
                println("Uploading ${mappingFile.get().asFile.absolutePath} to fantasy server...")
            }
        }
        android {
            
compileSdkVersion(29)
defaultConfig {
    minSdkVersion(21)
    targetSdkVersion(29)
}
            buildTypes {
                getByName("debug") {
                    isMinifyEnabled = true
                }
            }
        }
        androidComponents {
            onVariants { variant ->
                project.tasks.register<MappingFileUploadTask>("${variant.name}MappingFileUpload") {
                    mappingFile.set(variant.artifacts.get(SingleArtifact.OBFUSCATION_MAPPING_FILE))
                }
            }
        }