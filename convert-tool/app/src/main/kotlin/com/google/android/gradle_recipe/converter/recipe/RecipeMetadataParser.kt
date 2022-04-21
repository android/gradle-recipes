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

import org.tomlj.Toml
import org.tomlj.TomlParseResult
import java.io.File
import java.nio.file.Path

const val RECIPE_METADATA_FILE = "recipe_metadata.toml"

/** Recipe metadata parser, that reads and parses the metadata file
 * from the recipe folder
 */
class RecipeMetadataParser(recipeFolder: Path) {
    private var parseResult: TomlParseResult = Toml.parse(File(recipeFolder.toFile(), RECIPE_METADATA_FILE).toPath())
    val minAgpVersion: String
    val maxAgpVersion: String?
    val tasks: List<String>
    val indexKeywords: List<String>

    init {
        if (parseResult.hasErrors()) {
            System.err.println("Recipes error/s at $recipeFolder")
            parseResult.errors().forEach { error -> System.err.println(error.toString()) }
            throw IllegalArgumentException("Badly formatted recipe_metadata.toml file")
        }

        val minAgpVersionFromMetadata = parseResult.getString("agpVersion.min")

        if (minAgpVersionFromMetadata != null) {
            minAgpVersion = minAgpVersionFromMetadata
        } else {
            throw IllegalArgumentException("the minimal AGP version must be specified in the recipe metadata file")
        }

        maxAgpVersion = parseResult.getString("agpVersion.max")

        val tasksList = parseResult.getArray("gradleTasks.tasks")?.toList()
        tasks = tasksList?.let { list ->
            list.map {
                it as String
            }
        } ?: emptyList()

        val indexKeywordsList = parseResult.getArray("indexMetadata.index")?.toList()
        indexKeywords = indexKeywordsList?.let { list ->
            list.map {
                it as String
            }
        } ?: emptyList()
    }
}