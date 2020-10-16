/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.android.build.api.artifact.ArtifactType
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import com.android.build.api.dsl.CommonExtension

abstract class ExamplePlugin: Plugin<Project> {

    override fun apply(project: Project) {
        val gitVersionProvider =
            project.tasks.register("gitVersionProvider", GitVersionTask::class.java) {
                it.gitVersionOutputFile.set(
                    File(project.buildDir, "intermediates/gitVersionProvider/output")
                )
                it.outputs.upToDateWhen { false }
            }

        val android = project.extensions.getByType(CommonExtension::class.java)

        android.onVariantProperties {

            val manifestProducer =
                project.tasks.register(name + "ManifestProducer", ManifestProducerTask::class.java) {
                    it.gitInfoFile.set(gitVersionProvider.flatMap(GitVersionTask::gitVersionOutputFile))
                }
            artifacts.use(manifestProducer)
                .wiredWith(ManifestProducerTask::outputManifest)
                .toCreate(ArtifactType.MERGED_MANIFEST)

            project.tasks.register(name + "Verifier", VerifyManifestTask::class.java) {
                it.apkFolder.set(artifacts.get(ArtifactType.APK))
                it.builtArtifactsLoader.set(artifacts.getBuiltArtifactsLoader())
            }
        }
    }
}