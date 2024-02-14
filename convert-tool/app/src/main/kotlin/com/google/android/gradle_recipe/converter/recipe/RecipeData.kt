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

package com.google.android.gradle_recipe.converter.recipe

import com.github.rising3.semver.SemVer
import com.google.android.gradle_recipe.converter.converters.AgpVersion
import com.google.android.gradle_recipe.converter.converters.FullAgpVersion
import com.google.android.gradle_recipe.converter.converters.RecipeConverter
import com.google.android.gradle_recipe.converter.converters.ShortAgpVersion
import com.google.android.gradle_recipe.converter.converters.ShortAgpVersion.Companion.isShortVersion
import com.google.android.gradle_recipe.converter.converters.getVersionsFromAgp
import com.google.android.gradle_recipe.converter.printErrorAndTerminate
import org.tomlj.Toml
import org.tomlj.TomlParseResult
import java.io.File
import java.nio.file.Path
import kotlin.io.path.name

const val RECIPE_METADATA_FILE = "recipe_metadata.toml"

/**
 * Recipe Data representing the content of `recipe_metadata.toml`
 */
class RecipeData private constructor(
    /** The name of the recipe to show in the index */
    val indexName: String,
    /** the name of the folder that should contain the recipe */
    val destinationFolder: String,
    val minAgpVersion: FullAgpVersion,
    val maxAgpVersion: ShortAgpVersion?,
    val tasks: List<String>,
    val validationTasks: List<String>?,
    val keywords: List<String>,
) {
    fun isCompliantWithAgp(agpVersion: FullAgpVersion): Boolean {
        val min = minAgpVersion
        val max = maxAgpVersion

        return if (max != null) {
            agpVersion >= min && agpVersion.toShort() <= max
        } else {
            // when maxAgpVersion is not specified
            agpVersion >= min
        }
    }

    companion object {
        fun loadFrom(recipeFolder: Path, mode: RecipeConverter.Mode): RecipeData {
            val toml = recipeFolder.resolve(RECIPE_METADATA_FILE)
            val parseResult: TomlParseResult = Toml.parse(toml)

            if (parseResult.hasErrors()) {
                System.err.println("TOML Parsing error(s) for $toml:")
                parseResult.errors().forEach { error -> System.err.println(error.toString()) }
                printErrorAndTerminate("Unable to read $toml")
            }

            val indexName = if (mode == RecipeConverter.Mode.RELEASE) {
                val entry = parseResult.getString("indexName")
                if (entry.isNullOrBlank()) {
                    recipeFolder.name
                } else {
                    entry
                }
            } else {
                recipeFolder.name
            }

            val destinationFolder = if (mode == RecipeConverter.Mode.RELEASE) {
                val entry = parseResult.getString("destinationFolder")
                if (entry.isNullOrBlank()) {
                    recipeFolder.name
                } else {
                    // check there's no path separator in there
                    if (entry.contains('/')) {
                        printErrorAndTerminate("destinationFolder value ('$entry') cannot contain / character ($recipeFolder)")
                    }
                    entry
                }
            } else {
                recipeFolder.name
            }

            val minAgpString = parseResult.getString("agpVersion.min")
                ?: printErrorAndTerminate("Did not find mandatory 'agpVersion.min' in $toml")

            val minAgpVersion = if (minAgpString.isShortVersion()) {
                getVersionsFromAgp(ShortAgpVersion.of(minAgpString))?.agp
                    ?: printErrorAndTerminate("Unable to get published AGP version from '$minAgpString'")
            } else {
                FullAgpVersion.of(minAgpString)
            }

            return RecipeData(
                indexName = indexName,
                destinationFolder = destinationFolder,
                minAgpVersion = minAgpVersion,
                maxAgpVersion = parseResult.getString("agpVersion.max")?.let { ShortAgpVersion.of(it) },
                tasks = parseResult.getArray("gradleTasks.tasks")?.toList()?.map { it as String } ?: emptyList(),
                validationTasks = parseResult.getArray("gradleTasks.validationTasks")?.toList()?.map { it as String },
                keywords = parseResult.getArray("indexMetadata.index")?.toList()?.map { it as String } ?: emptyList()
            )
        }
    }
}

fun String.toMajorMinor(): String = SemVer.parse(this).run { "${this.major}.${this.minor}" }

fun isRecipeFolder(folder: Path) = File(folder.toFile(), RECIPE_METADATA_FILE).exists()
