import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.artifact.ArtifactType

abstract class ExamplePlugin: Plugin<Project> {

    override fun apply(project: Project) {

        val android = project.extensions.getByType(CommonExtension::class.java)

        android.onVariantProperties {

            val copyApksProvider = project.tasks.register("copy${name}Apks", CopyApksTask::class.java)

            val transformationRequest = artifacts.use(copyApksProvider)
                .wiredWithDirectories(
                    CopyApksTask::apkFolder,
                    CopyApksTask::outFolder)
                .toTransformMany(ArtifactType.APK)

            copyApksProvider.configure {
                it.transformationRequest.set(transformationRequest)
                it.outFolder.set(File("/usr/local/google/home/jedo/src/studio-4.1-dev/out/apiTests/BuildSrc/workerEnabledTransformation/build/acme_apks"))
            }
        }
    }
}