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

        abstract class DisplayBundleFileTask: DefaultTask() {
            @get:InputFile
            abstract val bundleFile: RegularFileProperty

            @TaskAction
            fun taskAction() {
                println("Got the Bundle  ${bundleFile.get().asFile.absolutePath}")
            }
        }
        android {
            
compileSdkVersion(29)
defaultConfig {
    minSdkVersion(21)
    targetSdkVersion(29)
}
            defaultConfig {
                versionCode = 3
            }
        }
        androidComponents {
            onVariants { variant ->
                project.tasks.register<DisplayBundleFileTask>("${variant.name}DisplayBundleFile") {
                    bundleFile.set(variant.artifacts.get(SingleArtifact.BUNDLE))
                }
            }
        }