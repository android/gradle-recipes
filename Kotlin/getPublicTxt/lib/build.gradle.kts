plugins {
    id("com.android.library")
    kotlin("android")
}

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.BuiltArtifactsLoader
import java.lang.RuntimeException
import java.util.Locale
import javax.inject.Inject

abstract class PublicResourcesValidatorTask: DefaultTask() {

    @get:InputFile
    abstract val publicAndroidResources: RegularFileProperty

    @get:InputFile
    abstract val expectedPublicResources: RegularFileProperty

    @get:OutputDirectory
    abstract val fakeOutput: DirectoryProperty

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    @TaskAction
    fun taskAction() {
        workerExecutor.noIsolation().submit(Action::class.java) {
            actual.set(publicAndroidResources)
            expected.set(expectedPublicResources)
        }
    }

    abstract class Action: WorkAction<Action.Parameters> {
        abstract class Parameters: WorkParameters {
            abstract val actual: RegularFileProperty
            abstract val expected: RegularFileProperty
        }
        override fun execute() {
            val actual = parameters.actual.get().asFile.readLines()
            val expected = parameters.expected.get().asFile.readLines()
            if (actual != expected) {
                throw RuntimeException(
                        "Public Android resources have changed.\n" +
                        "Please either revert the change or update the API expectation file\n" +
                        "Expected\n    " + expected.joinToString("\n    ") + "\n" +
                        "Actual\n    " + actual.joinToString("\n    ")
                    )
            }
            println("Public Android resources unchanged.")
        }
    }
}
android {
    namespace = "com.android.build.example.minimal"
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
    }
}
androidComponents {
    onVariants { variant ->
        @OptIn(ExperimentalStdlibApi::class)
        val capitalizedName = variant.name.capitalize(Locale.US)
        project.tasks.register<PublicResourcesValidatorTask>("validate${capitalizedName}PublicResources") {
            publicAndroidResources.set(variant.artifacts.get(SingleArtifact.PUBLIC_ANDROID_RESOURCES_LIST))
            expectedPublicResources.set(project.file("src/test/expectedApi/public-resources.txt"))
            fakeOutput.set(project.layout.buildDirectory.dir("intermediates/PublicResourcesValidatorTask/$name"))
        }
    }
}