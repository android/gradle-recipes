/*
 * Copyright (C) 2024 The Android Open Source Project
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

package com.google.android.gradle_recipe.converter.context

import com.google.android.gradle_recipe.converter.context.Context.VersionInfo
import com.google.android.gradle_recipe.converter.converters.FullAgpVersion
import com.google.android.gradle_recipe.converter.converters.ShortAgpVersion
import com.google.android.gradle_recipe.converter.findLatestVersion
import com.google.android.gradle_recipe.converter.printErrorAndTerminate
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.isRegularFile
import kotlin.io.path.readLines

// The position of the gradle-resources folder to take the Gradle wrapper
private const val GRADLE_RESOURCES_FOLDER = "gradle-resources"
private const val VERSION_MAPPING = "version_mappings.txt"

class DefaultContext(
    private val versionMappingFile: Path,
    override val gradleResourceFolder: Path,
    /** the maven metadata file. If not provided it'll be automatically downloaded */
    private val mavenMetadataFile: File? = null
): Context {

    override fun getPublishedAgp(agp: ShortAgpVersion): FullAgpVersion = shortToFullAgpVersionMap[agp]
        ?: printErrorAndTerminate("Unable to find Published AGP version for AGP version $agp - Make sure it's present in version_mappings.txt")

    override val maxPublishedAgp: FullAgpVersion by lazy {
        shortToFullAgpVersionMap.values.max()
    }

    override fun getGradleVersion(agp: ShortAgpVersion): String = versionMappings[agp]?.gradle
        ?: printErrorAndTerminate("Unable to find Gradle version for AGP version $agp - Make sure it's present in version_mappings.txt")

    override fun getKotlinVersion(agp: ShortAgpVersion): String = versionMappings[agp]?.kotlin
        ?: printErrorAndTerminate("Unable to find Kotlin version for AGP version $agp - Make sure it's present in version_mappings.txt")

    override val versionMappings: Map<ShortAgpVersion, VersionInfo> by lazy {
        if (!versionMappingFile.isRegularFile()) {
            printErrorAndTerminate("Missing AGP version mapping file at $versionMappingFile")
        }

        // split the lines into list of 3 versions
        val versionList = versionMappingFile
            .readLines()
            .asSequence()
            .filter { !it.startsWith("#") }
            .map { it.split(";")}.toList()

        val map = mutableMapOf<ShortAgpVersion, VersionInfo>()

        for (versions in versionList) {
            val shortAgpVersion = ShortAgpVersion.ofOrNull(versions[0])
                ?: throw RuntimeException("unable to parse short AGP version ${versions[0]}")

            val versionInfo = VersionInfo(
                gradle = versions[1],
                kotlin = versions[2]
            )

            map[shortAgpVersion] = versionInfo
        }

        map
    }

    private val shortToFullAgpVersionMap: Map<ShortAgpVersion, FullAgpVersion> by lazy {
        val inputStream = mavenMetadataFile?.let { FileInputStream(it) } ?: downloadAgpMavenMetadata()

        // get the published AGP for each version (when available), and convert to the final format
        findLatestVersion(
            inputStream,
            versionMappings.keys.map { it.toString()
            }).entries.map { ShortAgpVersion.ofOrNull(it.key)!! to it.value }
            .toMap()
    }

    private fun downloadAgpMavenMetadata(): InputStream {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://dl.google.com/dl/android/maven2/com/android/tools/build/gradle/maven-metadata.xml")
            .build();

        val response = client.newCall(request).execute()

        return response.body()?.byteStream() ?: throw RuntimeException("Failed to download AGP version information from gmaven")
    }

    companion object {

        /**
         * Local context based on finding the git root folder and downloading the maven file
         */
        val localContext: DefaultContext by lazy {
            val rootFolder = computeGitRootFolder()
            createFromCustomRoot(rootFolder)
        }

        fun createFromCustomRoot(rootFolder: Path): DefaultContext {
            return DefaultContext(
                rootFolder.resolve(VERSION_MAPPING),
                rootFolder.resolve(GRADLE_RESOURCES_FOLDER),
                mavenMetadataFile = null
            )
        }

        /**
         * Compute the root of the git project, in order to find files needed by the conversion logic.
         *
         * The logic will vary based on where the tool's jar is located. On the github workflow, this run from
         * a different path.
         */
        private fun computeGitRootFolder(): Path {
            val url = DefaultContext::class.java.protectionDomain.codeSource.location
            val path = Path.of(url.toURI())

            val standaloneJar = System.getenv("STANDALONE_JAR") != null

            if (standaloneJar) {
                // The path is going to be $ROOT/convert-tool/app/build/libs/recipes-converter.jar
                // we want to return $ROOT
                return path.resolve("../../../../../").normalize()
            } else {
                // The path is going to be $ROOT/convert-tool/app/build/install/convert-tool/lib/recipes-converter.jar
                // we want to return $ROOT
                return path.resolve("../../../../../../../").normalize()
            }
        }
    }
}