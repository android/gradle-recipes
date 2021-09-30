plugins {
        id("com.android.application")
        kotlin("android")
        kotlin("android.extensions")
}
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import com.android.build.api.variant.BuiltArtifactsLoader
import com.android.build.api.artifact.SingleArtifact
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
    }
    signingConfigs {

            create("default") {
                    keyAlias = "pretend"
                    keyPassword = "some password"
                    storeFile = file("/path/to/supposedly/existing/keystore.jks")
                    storePassword = "some keystore password"
            }
            create("other") {
                    keyAlias = "invalid"
                    keyPassword = "some password"
                    storeFile = file("/path/to/some/other/keystore.jks")
                    storePassword = "some keystore password"
            }
    }
    flavorDimensions("version")
        buildTypes {
                create("special")
        }
        productFlavors {
                create("flavor1") {
                        dimension = "version"
                        signingConfig = signingConfigs.getByName("default")
                }
                create("flavor2") {
                        dimension = "version"
                        signingConfig = signingConfigs.getByName("default")
                }
        }
}

androidComponents {
    onVariants(selector()
            .withFlavor("version" to "flavor1")
            .withBuildType("special")
    ) { variant ->
            variant.signingConfig?.setConfig(android.signingConfigs.getByName("other"))
    }
}