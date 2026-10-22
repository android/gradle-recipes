/*
 * Copyright 2026 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.SigningConfigInfo
import com.android.build.gradle.AppPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import java.io.File
import java.security.cert.X509Certificate
import java.util.Properties
import java.util.jar.JarFile

/**
 * This custom plugin will register a callback that is applied to all variants.
 */
class CustomPlugin : Plugin<Project> {
  override fun apply(project: Project) {

    // Registers a callback on the application of the Android Application plugin.
    // This allows the CustomPlugin to work whether it's applied before or after
    // the Android Application plugin.
    project.plugins.withType(AppPlugin::class.java) {

      // Queries for the extension set by the Android Application plugin.
      // This is the second of two entry points into the Android Gradle plugin
      val androidComponents =
        project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
      // Registers a callback to be called, when a new variant is configured
      androidComponents.onVariants { variant ->

        val lazySigningConfig = project.providers.of(SigningConfigValueSource::class.java) { spec ->
          spec.parameters.propertiesFile.set(project.layout.projectDirectory.file("../signing.properties"))
        }
        variant.signingConfig.from(lazySigningConfig)

        // -- Verification --
        // the following is just to validate the recipe and is not actually
        // part of the recipe itself
        project.tasks.register<TemplateTask>("validate${variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}") {
          apkFolder.set(variant.artifacts.get(SingleArtifact.APK))
        }
      }
    }
  }

  abstract class SigningConfigValueSource : ValueSource<SigningConfigInfo, SigningConfigValueSource.Params> {
    interface Params : ValueSourceParameters {
      val propertiesFile: RegularFileProperty
    }

    override fun obtain(): SigningConfigInfo? {
      val file = parameters.propertiesFile.orNull?.asFile ?: return null

      if (!file.exists()) return null
      val props = Properties().apply {
        file.inputStream().use { load(it) }
      }
      val storeFileProp = props.getProperty("storeFile") ?: return null
      var storeFile = File(storeFileProp)
      if (!storeFile.isAbsolute) {
        storeFile = File(file.parentFile, storeFileProp)
      }
      return SigningConfigInfo(
        storeFile,
        props.getProperty("storePassword"),
        props.getProperty("keyAlias"),
        props.getProperty("keyPassword"),
        props.getProperty("storeType")
      )
    }
  }
}

/**
 * Validation task to verify the behavior of the recipe
 */
abstract class TemplateTask : DefaultTask() {
  @get:InputDirectory
  abstract val apkFolder: DirectoryProperty

  @TaskAction
  fun taskAction() {
    val apkFiles = apkFolder.get().asFile.listFiles { file -> file.extension == "apk" }
      ?: throw RuntimeException("No APK files found in directory ${apkFolder.get().asFile}")
    if (apkFiles.isEmpty()) {
      throw RuntimeException("No APK files found in directory ${apkFolder.get().asFile}")
    }
    var verified = false
    for (apkFile in apkFiles) {
      JarFile(apkFile).use { jarFile ->
        val entry = jarFile.getJarEntry("AndroidManifest.xml") ?: return@use
        // Read the entry content to populate certificates
        jarFile.getInputStream(entry).use { isStream ->
          val buffer = ByteArray(8192)
          while (isStream.read(buffer) != -1) {
            // do nothing
          }
        }
        val certs = entry.certificates
        if (certs != null) {
          for (cert in certs) {
            if (cert is X509Certificate) {
              val subjectName = cert.subjectX500Principal.name
              val hasGoogleOrg = subjectName.split(",")
                .any { it.trim().equals("O=Google", ignoreCase = true) }
              if (hasGoogleOrg) {
                verified = true
                break
              }
            }
          }
        }
      }
      if (verified) break
    }
    if (!verified) {
      throw RuntimeException("APK is not signed with the expected signing key.")
    }
  }
}