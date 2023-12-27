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

import com.google.android.gradle_recipe.converter.converters.RecipeConverter.Mode
import com.google.android.gradle_recipe.converter.printErrorAndTerminate
import com.google.android.gradle_recipe.converter.recipe.visitRecipes
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists

private const val INDEX_METADATA_FILE = "README.md"

private val THEME_ORDER = listOf("Themes", "APIs", "Call chains")

private const val COMMA_DELIMITER = ", "

/**
 * Recursive converter converts recipes from source of truth mode
 * to release mode
 */
class RecursiveConverter(
    private val agpVersion: String?,
    private var repoLocation: String?,
    var gradleVersion: String?,
    var gradlePath: String?,
    private val branchRoot: Path,
) {

    // dual level map. The first level is the category of keywords, the 2nd is the keywords themselves
    private val keywordMap = mutableMapOf<String, MutableMap<String, MutableList<IndexData>>>()

    private data class IndexData(
        val title: String,
        val link: String
    )

    fun convertAllRecipes(sourceAll: Path, destination: Path) {
        if (!sourceAll.exists()) {
            printErrorAndTerminate("the source $sourceAll folder doesn't exist")
        }

        val recipeConverter = RecipeConverter(
            agpVersion = agpVersion,
            repoLocation = repoLocation,
            gradleVersion = gradleVersion,
            gradlePath = gradlePath,
            mode = Mode.RELEASE,
            branchRoot = branchRoot,
        )

        visitRecipes(sourceAll) { recipeFolder: Path ->
            val conversionResult = recipeConverter.convert(recipeFolder, destination)

            if (conversionResult.result == ResultMode.SUCCESS) {
                for (keyword in conversionResult.recipeData.keywords) {

                    // split the keyword in category/keyword if there is one
                    val splits = keyword.split('/')
                    val (category, kw) = when (splits.size) {
                        1 -> "Others" to keyword
                        2 -> splits[0] to splits[1]
                        else -> error("Index entries should contain at most one '/' character: $keyword from $recipeFolder")
                    }

                    val secondaryMap = keywordMap.computeIfAbsent(category) { mutableMapOf() }

                    val list = secondaryMap.computeIfAbsent(kw) { mutableListOf() }
                    list.add(
                        IndexData(
                            conversionResult.recipeData.indexName,
                            conversionResult.recipeData.destinationFolder
                        )
                    )
                }
            }
        }

        // agpVersion is always true in release mode
        writeRecipesIndexFile(keywordMap, destination, agpVersion!!)
    }

    private fun writeRecipesIndexFile(
        map: MutableMap<String, MutableMap<String, MutableList<IndexData>>>,
        destination: Path,
        agpVersion: String,
    ) {
        val builder = StringBuilder()
        builder.appendLine("""
            # Recipes for AGP version `$agpVersion`
            This branch contains recipes compatible with AGP $agpVersion. If you want to find recipes
            for other AGP versions, switch to the corresponding `agp-*` branch.

            This branch is read only. Contributions are only accepted on the `studio-main` branch. See `CONTRIBUTION.md`
            there.
        """.trimIndent())
        builder.appendLine("# Recipes Index")

        builder.appendLine("Index is organized in categories, offering different ways to reach the recipe you want.")

        // we want to process some known categories first, in a specific order that's not alphabetical
        val unknownCategories = map.keys - THEME_ORDER

        THEME_ORDER.forEach {
            processCategory(builder, it, map)
        }

        unknownCategories.sorted().forEach { category ->
            processCategory(builder, category, map)
        }

        builder.appendLine("""
            # License
            ```
            Copyright 2022 The Android Open Source Project

            Licensed under the Apache License, Version 2.0 (the "License");
            you may not use this file except in compliance with the License.
            You may obtain a copy of the License at

                https://www.apache.org/licenses/LICENSE-2.0

            Unless required by applicable law or agreed to in writing, software
            distributed under the License is distributed on an "AS IS" BASIS,
            WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
            See the License for the specific language governing permissions and
            limitations under the License.
            ```
        """.trimIndent())

        File(
            destination.resolve(INDEX_METADATA_FILE).toUri()
        ).writeText(builder.toString())
    }

    private fun processCategory(
        stringBuilder: StringBuilder,
        category: String,
        map: MutableMap<String, MutableMap<String, MutableList<IndexData>>>
    ) {
        val secondaryMap = map[category] ?: return
        if (secondaryMap.isEmpty()) return

        stringBuilder.appendLine("## $category")

        secondaryMap.keys.sorted().forEach { keyword ->
            stringBuilder.append("* $keyword - ")

            val joiner = StringJoiner(COMMA_DELIMITER)

            secondaryMap[keyword]?.forEach { data ->
                joiner.add("[${data.title}](${data.link})")
            }

            stringBuilder.appendLine(joiner.toString())
        }
    }
}