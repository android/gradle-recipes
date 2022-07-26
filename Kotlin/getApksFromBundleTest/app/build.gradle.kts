plugins {
        id("com.android.application")
        kotlin("android")
}
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import com.android.build.api.artifact.SingleArtifact
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
abstract class DisplayApkFromBundleTask: DefaultTask() {
    @get:InputFile
    abstract val apkFromBundle: RegularFileProperty

    @TaskAction
    fun taskAction() {
        println("Got a Universal APK " + apkFromBundle.get().asFile.canonicalPath)
    }
}
android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        versionCode = 1
    }
    namespace = "com.example.addjavasource"
}

androidComponents {
    onVariants { variant ->
        project.tasks.register<DisplayApkFromBundleTask>("${variant.name}DisplayApkFromBundle") {
            apkFromBundle.set(variant.artifacts.get(SingleArtifact.APK_FROM_BUNDLE))
        }
    }
}