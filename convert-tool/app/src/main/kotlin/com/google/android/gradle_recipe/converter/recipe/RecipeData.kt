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
import org.tomlj.Toml
import org.tomlj.TomlParseResult
import java.io.File
import java.nio.file.Path

private const val RECIPE_METADATA_FILE = "recipe_metadata.toml"

/**
 * Recipe Data representing the content of `recipe_metadata.toml`
 */
class RecipeData private constructor(
    val title: String,
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
        fun loadFrom(recipeFolder: Path): RecipeData {
            val toml = recipeFolder.resolve(RECIPE_METADATA_FILE)
            val parseResult: TomlParseResult = Toml.parse(toml)

            if (parseResult.hasErrors()) {
                System.err.println("TOML Parsing error(s) for $toml:")
                parseResult.errors().forEach { error -> System.err.println(error.toString()) }
                throw IllegalArgumentException("Unable to read $toml")
            }

            return RecipeData(
                title = parseResult.getString("title") ?: error("Did not find mandatory 'title` entry in $toml"),
                minAgpVersion = parseResult.getString("agpVersion.min")
                    ?: error("Did not find mandatory 'agpVersion.min' in $toml"),
                maxAgpVersion = parseResult.getString("agpVersion.max"),
                tasks = parseResult.getArray("gradleTasks.tasks")?.toList()?.map { it as String } ?: emptyList(),
                keywords = parseResult.getArray("indexMetadata.index")?.toList()?.map { it as String } ?: emptyList()
            )
        }
    }
}

fun String.toMajorMinor(): String = SemVer.parse(this).run { "${this.major}.${this.minor}" }

fun isRecipeFolder(folder: Path) = File(folder.toFile(), RECIPE_METADATA_FILE).exists()
