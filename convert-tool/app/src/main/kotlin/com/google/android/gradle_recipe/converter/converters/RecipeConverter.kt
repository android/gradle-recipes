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
import java.io.File
import java.io.IOException
import java.lang.System.err
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

val agpToGradleVersions = mapOf(
    "3.6" to "5.6",
    "4.0" to "6.1.1",
    "4.1" to "6.5",
    "4.2" to "6.7.1",
    "7.0" to "7.0",
    "7.1" to "7.2",
    "7.2" to "7.3.3",
    "7.3" to "7.4",
    "7.4" to "7.5",
    "8.1" to "8.0-rc-1",
)

/**
 * Current supported Kotlin plugin, later we add a
 * CLI argument to support more versions
 */
const val kotlinPluginVersion = "1.9.0-Beta"

/**
 * The compile SDK version for recipes
 */
const val compileSdkVersion = "34"

/**
 * The minimum SDK version for recipes
 */
const val minimumSdkVersion = "21"

fun convertStringToMode(modeFromString: String?): RecipeConverter.Mode {
    return if (modeFromString != null) {
        RecipeConverter.Mode.valueOf(modeFromString.toString().uppercase())
    } else {
        RecipeConverter.Mode.RELEASE
    }
}

data class ConversionResult(val recipe: Recipe, var isConversionSuccessful: Boolean)

/**
 *  Converts the individual recipe, calculation the conversion mode by input parameters
 */
class RecipeConverter(
    val agpVersion: String?,
    repoLocation: String?,
    gradleVersion: String?,
    gradlePath: String?,
    mode: Mode,
    private val overwrite: Boolean,
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
                WorkingCopyConverter()
            }

            Mode.SOURCE -> {
                SourceConverter()
            }

            Mode.RELEASE -> {
                ReleaseConverter(
                    agpVersion = agpVersion ?: error("Must specify the AGP version for release"),
                    gradleVersion = gradleVersion,
                    repoLocation = repoLocation,
                    gradlePath = gradlePath
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

        val result = ConversionResult(recipe, false)

        if (converter.isConversionCompliant(recipe)) {
            converter.setRecipe(recipe)
            result.isConversionSuccessful = true

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
                            converter.copyGradleFolder(destinationFile.parent)
                        }

                        "settings.gradle.kts" -> {
                            converter.convertSettingsGradleKts(sourceFile, destinationFile)
                            converter.copyGradleFolder(destinationFile.parent)
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
        } else {
            err.println("Couldn't convert $source due to AGP version compliance ")
        }

        return result
    }

    @Throws(IOException::class)
    fun isEmpty(path: Path): Boolean {
        if (Files.isDirectory(path)) {
            Files.list(path).use { entries -> return !entries.findFirst().isPresent }
        }
        return false
    }
}
