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

import com.google.android.gradle_recipe.converter.deleteNonHiddenRecursively
import com.google.android.gradle_recipe.converter.printErrorAndTerminate
import com.google.android.gradle_recipe.converter.recipe.RecipeData
import com.google.android.gradle_recipe.converter.recipe.toMajorMinor
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
            printErrorAndTerminate("Missing AGP version mapping file at $file")
        }

        val lines = file
            .readLines()
            .asSequence()
            .filter { !it.startsWith("#") }

        agpToVersionsMap = lines
            .map {
                val values = it.split(";")
                values[0] to VersionInfo(
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

enum class ResultMode {
    SUCCESS, FAILURE, SKIPPED
}

data class ConversionResult(val recipeData: RecipeData, val result: ResultMode)

/**
 *  Converts the individual recipe, calculation the conversion mode by input parameters
 */
class RecipeConverter(
    val agpVersion: String?,
    repoLocation: String?,
    gradleVersion: String?,
    gradlePath: String?,
    private val mode: Mode,
    branchRoot: Path,
    private val generateWrapper: Boolean = true,
) {
    private val converter: Converter

    enum class Mode {
        RELEASE, WORKINGCOPY, SOURCE
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
                    agpVersion = agpVersion ?: printErrorAndTerminate("Must specify the AGP version for release"),
                    gradleVersion = gradleVersion,
                    repoLocation = repoLocation,
                    gradlePath = gradlePath,
                    branchRoot = branchRoot,
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

        val success = if (converter.isConversionCompliant(recipeData)) {
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

        return ConversionResult(recipeData, success)
    }
}

private fun Path.isNotEmpty(): Boolean = Files.list(this).findAny().isPresent
