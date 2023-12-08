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

import com.google.android.gradle_recipe.converter.recipe.Recipe
import com.google.android.gradle_recipe.converter.recipe.RecipeMetadataParser
import com.google.android.gradle_recipe.converter.recipe.toMajorMinor
import java.io.File
import java.io.IOException
import java.lang.System.err
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.readLines

private const val VERSION_MAPPING = "version_mappings.txt"

data class VersionInfo(
    val agp: String,
    val gradle: String,
    val kotlin: String
)

private lateinit var agpToVersionsMap: Map<String, VersionInfo>
private lateinit var maxAgp: String
fun getVersionsFromAgp(branchRoot: Path, agp: String): VersionInfo? {
    initAgpToGradleMap(branchRoot)
    return agpToVersionsMap[agp].also {
        if (it == null) {
            println(agpToVersionsMap.entries)
        }
    }
}

fun getMaxAgp(branchRoot: Path): String {
    initAgpToGradleMap(branchRoot)
    return maxAgp
}

@Synchronized
private fun initAgpToGradleMap(branchRoot: Path) {
    if (!::agpToVersionsMap.isInitialized) {
        val file = branchRoot.resolve(VERSION_MAPPING)
        if (!file.isRegularFile()) {
            throw RuntimeException("Missing AGP version mapping file at $file")
        }

        val lines = file
            .readLines()
            .asSequence()
            .filter { !it.startsWith("#") }

        agpToVersionsMap = lines
            .map {
                val values = it.split(";")
                values[0].toMajorMinor() to VersionInfo(
                    agp = values[0],
                    gradle = values[1],
                    kotlin = values[2]
                )
            }.toMap()

        maxAgp = lines.map { it.split(";")[0] }.max()
    }
}

/**
 * The compile SDK version for recipes
 */
const val compileSdkVersion = "34"

/**
 * The minimum SDK version for recipes
 */
const val minimumSdkVersion = "21"

data class ConversionResult(val recipe: Recipe, val isConversionSuccessful: Boolean)

/**
 *  Converts the individual recipe, calculation the conversion mode by input parameters
 */
class RecipeConverter(
    val agpVersion: String?,
    repoLocation: String?,
    gradleVersion: String?,
    gradlePath: String?,
    private val mode: Mode,
    private val overwrite: Boolean,
    branchRoot: Path,
    private val generateWrapper: Boolean = true,
) {
    private val converter: Converter

    enum class Mode {
        RELEASE, WORKINGCOPY, SOURCE
    }

    /** A filter for files and folders during a conversion. Filters out Gradle
     *  and Android Studio temporary and local files.
     */
    companion object {
        private val skippedFilenames = setOf("gradlew", "gradlew.bat", "local.properties")
        private val skippedFoldernames = setOf("build", ".idea", ".gradle", "out", "wrapper")

        fun accept(file: File): Boolean {
            if (file.isFile) {
                return !skippedFilenames.contains(file.name)
            }

            if (file.isDirectory) {
                return !skippedFoldernames.contains(file.name)
            }

            return true
        }
    }

    init {
        converter = when (mode) {
            Mode.WORKINGCOPY -> {
                WorkingCopyConverter(branchRoot)
            }

            Mode.SOURCE -> {
                SourceConverter(branchRoot)
            }

            Mode.RELEASE -> {
                ReleaseConverter(
                    agpVersion = agpVersion ?: error("Must specify the AGP version for release"),
                    gradleVersion = gradleVersion,
                    repoLocation = repoLocation,
                    gradlePath = gradlePath,
                    branchRoot = branchRoot,
                )
            }
        }
    }

    @Throws(IOException::class)
    fun convert(source: Path, destination: Path): ConversionResult {
        if (!source.isDirectory()) {
            error("the source $source folder is not a directory")
        }

        if (destination.exists() && !isEmpty(destination)) {
            if (!overwrite) {
                error("the destination $destination folder is not empty, call converter with --overwrite to overwrite it")
            } else {
                destination.toFile().deleteRecursively()
            }
        }

        val metadataParser = RecipeMetadataParser(source)
        val recipe = Recipe(
            minAgpVersion = metadataParser.minAgpVersion,
            maxAgpVersion = metadataParser.maxAgpVersion,
            tasks = metadataParser.tasks,
            keywords = metadataParser.indexKeywords
        )

        val success = if (converter.isConversionCompliant(recipe)) {
            converter.recipe = recipe

            Files.walkFileTree(source, object : SimpleFileVisitor<Path>() {
                @Throws(IOException::class)
                override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                    if (accept(dir.toFile())) {
                        Files.createDirectories(destination.resolve(source.relativize(dir)))
                        return FileVisitResult.CONTINUE
                    }

                    return FileVisitResult.SKIP_SUBTREE
                }

                @Throws(IOException::class)
                override fun visitFile(sourceFile: Path, attrs: BasicFileAttributes): FileVisitResult {
                    val fileName = sourceFile.fileName.toString()
                    val destinationFile = destination.resolve(source.relativize(sourceFile))

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
                            if (accept(sourceFile.toFile())) {
                                Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING)
                            }
                        }
                    }

                    return FileVisitResult.CONTINUE
                }
            })

            if (generateWrapper && mode != Mode.SOURCE) {
                converter.copyGradleFolder(destination)
            }

            true
        } else {
            err.println("Couldn't convert $source due to AGP version compliance ")
            false
        }

        return ConversionResult(recipe, success)
    }

    @Throws(IOException::class)
    fun isEmpty(path: Path): Boolean {
        if (Files.isDirectory(path)) {
            Files.list(path).use { entries -> return !entries.findFirst().isPresent }
        }
        return false
    }
}