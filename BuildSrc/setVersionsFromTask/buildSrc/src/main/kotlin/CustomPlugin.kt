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
import com.android.build.api.artifact.Artifacts
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.VariantOutputConfiguration.OutputType
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class CustomPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withType(AppPlugin::class.java) {
            val extension = project.extensions.getByName("androidComponents") as ApplicationAndroidComponentsExtension
            extension.configure(project)
        }
    }
}

fun ApplicationAndroidComponentsExtension.configure(project: Project) {
    // Note: Everything in there is incubating.

    // onVariants registers an action that configures variant properties during
    // variant computation (which happens during afterEvaluate)
    onVariants {
        // applies to all variants. This excludes test components (unit test and androidTest)
    }

    // use filter to apply onVariants to a subset of the variants
    onVariants(selector().withBuildType("release")) { variant ->
        // Because app module can have multiple output when using mutli-APK, versionCode/Name
        // are only available on the variant output.
        // Here gather the output when we are in single mode (ie no multi-apk)
        val mainOutput = variant.outputs.single { it.outputType == OutputType.SINGLE }

        // create version Code generating task
        val versionCodeTask = project.tasks.register("computeVersionCodeFor${variant.name}", VersionCodeTask::class.java) {
            it.outputFile.set(project.layout.buildDirectory.file("versionCode.txt"))
        }

        // wire version code from the task output
        // map will create a lazy Provider that
        // 1. runs just before the consumer(s), ensuring that the producer (VersionCodeTask) has run
        //    and therefore the file is created.
        // 2. contains task dependency information so that the consumer(s) run after the producer.
        mainOutput.versionCode.set(versionCodeTask.flatMap { it.outputFile.map { it.asFile.readText().toInt() } })

        // same for version Name
        val versionNameTask = project.tasks.register("computeVersionNameFor${variant.name}", VersionNameTask::class.java) {
            it.outputFile.set(project.layout.buildDirectory.file("versionName.txt"))
        }
        mainOutput.versionName.set(versionNameTask.flatMap { it.outputFile.map { it.asFile.readText() }})

        // finally add the verifier task that will check that the merged manifest
        // does contain the version code and version name from the tasks added
        // above.
        project.tasks.register("verifierFor${variant.name}", VerifyManifestTask::class.java) {
            it.apkFolder.set(variant.artifacts.get(SingleArtifact.APK))
            it.builtArtifactsLoader.set(variant.artifacts.getBuiltArtifactsLoader())
        }
    }
}