/*
 * Copyright 2022 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gradle_recipe.converter.converters

import com.google.android.gradle_recipe.converter.branchRoot
import com.google.android.gradle_recipe.converter.deleteNonHiddenRecursively
import com.google.android.gradle_recipe.converter.findLatestVersion
import com.google.android.gradle_recipe.converter.printErrorAndTerminate
import com.google.android.gradle_recipe.converter.recipe.RecipeData
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.readLines

private const val VERSION_MAPPING = "version_mappings.txt"


data class VersionInfo(
    val agp: FullAgpVersion,
    val gradle: String,
    val kotlin: String
)

private lateinit var agpToVersionsMap: Map<ShortAgpVersion, VersionInfo>
private lateinit var maxAgp: FullAgpVersion

fun getVersionsFromAgp(agp: ShortAgpVersion): VersionInfo? {
    initAgpToGradleMap()
    return agpToVersionsMap[agp].also {
        if (it == null) {
            println(agpToVersionsMap.entries)
        }
    }
}

fun getMaxAgp(): FullAgpVersion {
    initAgpToGradleMap()
    return maxAgp
}

@Synchronized
private fun initAgpToGradleMap() {
    if (!::agpToVersionsMap.isInitialized) {
        val file = branchRoot.resolve(VERSION_MAPPING)
        if (!file.isRegularFile()) {
            printErrorAndTerminate("Missing AGP version mapping file at $file")
        }

        // split the lines into list of 3 versions
        val versionList = file
            .readLines()
            .asSequence()
            .filter { !it.startsWith("#") }
            .map { it.split(";")}.toList()

        // get the published AGP for each version
        // download the AGP maven-metadata.xml file
        val publishedAgpMap = findLatestVersion(getAgpVersionData(), versionList.map { it[0]} )

        // iterate through the list of version, which should be ordered, and keep track of last one.
        var lastVersionInfo: VersionInfo? = null

        val map = mutableMapOf<ShortAgpVersion, VersionInfo>()

        for (versions in versionList) {
            val versionInfo = VersionInfo(
                // TODO handle the case where there is no published version (e.g. new API in yet unpublished AGP)
                agp = FullAgpVersion.of(publishedAgpMap[versions[0]] ?: versions[0]),
                gradle = versions[1],
                kotlin = versions[2]
            )
            lastVersionInfo = versionInfo
            map[ShortAgpVersion.of(versions[0])] = versionInfo
        }

        // max is the last lime read
        maxAgp = lastVersionInfo?.agp ?: throw RuntimeException("No AGP info found in $VERSION_MAPPING")

        agpToVersionsMap = map.toMap()
    }
}

private fun getAgpVersionData(): InputStream {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url("https://dl.google.com/dl/android/maven2/com/android/tools/build/gradle/maven-metadata.xml")
        .build();

    val response = client.newCall(request).execute()

    return response.body()?.byteStream() ?: throw RuntimeException("Failed to download AGP version information from gmaven")
}

/**
 * The compile SDK version for recipes
 */
const val compileSdkVersion = "34"

/**
 * The minimum SDK version for recipes
 */
const val minimumSdkVersion = "21"

enum class ResultMode {
    SUCCESS, FAILURE, SKIPPED
}

data class ConversionResult(val recipeData: RecipeData, val resultMode: ResultMode)

/**
 *  Converts the individual recipe, calculation the conversion mode by input parameters
 */
class RecipeConverter(
    val agpVersion: FullAgpVersion?,
    repoLocation: String?,
    gradleVersion: String?,
    gradlePath: String?,
    private val mode: Mode,
    private val generateWrapper: Boolean = true,
) {
    private val converter: Converter

    enum class Mode {
        RELEASE, WORKINGCOPY, SOURCE
    }

    init {
        converter = when (mode) {
            Mode.WORKINGCOPY -> {
                WorkingCopyConverter()
            }

            Mode.SOURCE -> {
                SourceConverter()
            }

            Mode.RELEASE -> {
                ReleaseConverter(
                    agpVersion = agpVersion ?: printErrorAndTerminate("Must specify the AGP version for release"),
                    gradleVersion = gradleVersion,
                    repoLocation = repoLocation,
                    gradlePath = gradlePath,
                )
            }
        }
    }

    /**
     * Converts a recipe from [source] into [destination]
     *
     * @param source the source folder containing the recipe.
     * @param destination the destination folder. A new folder will be created inside to contain the recipe
     *
     */
    fun convert(source: Path, destination: Path, overwrite: Boolean = false): ConversionResult {
        if (!source.isDirectory()) {
            printErrorAndTerminate("Source $source is not a directory!")
        }

        val recipeData = RecipeData.loadFrom(source, mode)

        val recipeDestination = destination.resolve(recipeData.destinationFolder)

        if (recipeDestination.isRegularFile()) {
            printErrorAndTerminate("Destination $recipeDestination exist but is not a folder!")
        }

        if (recipeDestination.isDirectory() && recipeDestination.isNotEmpty()) {
            if (!overwrite) {
                printErrorAndTerminate("Destination $recipeDestination folder is not empty, call converter with --overwrite to overwrite it")
            } else {
                recipeDestination.deleteNonHiddenRecursively()
            }
        }

        val resultMode = if (converter.isConversionCompliant(recipeData)) {
            if (mode == Mode.WORKINGCOPY) {
                converter.minAgp = recipeData.minAgpVersion
            }

            Files.walkFileTree(source, object : SimpleFileVisitor<Path>() {
                override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                    if (converter.accept(dir.toFile())) {
                        Files.createDirectories(recipeDestination.resolve(source.relativize(dir)))
                        return FileVisitResult.CONTINUE
                    }

                    return FileVisitResult.SKIP_SUBTREE
                }

                override fun visitFile(sourceFile: Path, attrs: BasicFileAttributes): FileVisitResult {
                    val fileName = sourceFile.fileName.toString()
                    val destinationFile = recipeDestination.resolve(source.relativize(sourceFile))

                    when (fileName) {
                        "build.gradle" -> {
                            converter.convertBuildGradle(sourceFile, destinationFile)
                        }

                        "build.gradle.kts" -> {
                            converter.convertBuildGradleKts(sourceFile, destinationFile)
                        }

                        "settings.gradle" -> {
                            converter.convertSettingsGradle(sourceFile, destinationFile)
                        }

                        "settings.gradle.kts" -> {
                            converter.convertSettingsGradleKts(sourceFile, destinationFile)
                        }

                        "libs.versions.toml" -> {
                            converter.convertVersionCatalog(sourceFile, destinationFile)
                        }

                        "build.libs.versions.toml" -> {
                            converter.convertVersionCatalog(sourceFile, destinationFile)
                        }

                        else -> {
                            if (converter.accept(sourceFile.toFile())) {
                                Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING)
                            }
                        }
                    }

                    return FileVisitResult.CONTINUE
                }
            })

            if (generateWrapper && mode != Mode.SOURCE) {
                converter.copyGradleFolder(recipeDestination)
            }

            converter.minAgp = null

            ResultMode.SUCCESS
        } else {
            println("Couldn't convert $source due to AGP version compliance ")
            ResultMode.SKIPPED
        }

        return ConversionResult(recipeData, resultMode)
    }
}

private fun Path.isNotEmpty(): Boolean = Files.list(this).findAny().isPresent
