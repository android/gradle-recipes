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
import com.google.android.gradle_recipe.converter.converters.RecipeConverter
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
    val minAgpVersion: String,
    val maxAgpVersion: String?,
    val tasks: List<String>,
    val keywords: List<String>,
) {
    fun isCompliantWithAgp(agpVersion: String): Boolean {
        val min = minAgpVersion
        val max = maxAgpVersion

        return if (max != null) {
            SemVer.parse(agpVersion) >= SemVer.parse(min) && SemVer.parse(agpVersion) <= SemVer.parse(max)
        } else {
            // when maxAgpVersion is not specified
            SemVer.parse(agpVersion) >= SemVer.parse(min)
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

            return RecipeData(
                indexName = indexName,
                destinationFolder = destinationFolder,
                minAgpVersion = parseResult.getString("agpVersion.min")
                    ?: printErrorAndTerminate("Did not find mandatory 'agpVersion.min' in $toml"),
                maxAgpVersion = parseResult.getString("agpVersion.max"),
                tasks = parseResult.getArray("gradleTasks.tasks")?.toList()?.map { it as String } ?: emptyList(),
                keywords = parseResult.getArray("indexMetadata.index")?.toList()?.map { it as String } ?: emptyList()
            )
        }
    }
}

fun String.toMajorMinor(): String = SemVer.parse(this).run { "${this.major}.${this.minor}" }

fun isRecipeFolder(folder: Path) = File(folder.toFile(), RECIPE_METADATA_FILE).exists()
