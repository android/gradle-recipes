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
import com.google.android.gradle_recipe.converter.recipe.visitRecipes
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists

const val INDEX_METADATA_FILE = "README.md"

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

    private val keywordsToRecipePaths = mutableMapOf<String, MutableList<IndexData>>()

    private data class IndexData(
        val title: String,
        val link: String
    )

    @Throws(IOException::class)
    fun convertAllRecipes(sourceAll: Path, destination: Path) {
        if (!sourceAll.exists()) {
            error("the source $sourceAll folder doesn't exist")
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

            if (conversionResult.isConversionSuccessful) {
                for (keyword in conversionResult.recipeData.keywords) {
                    val list = keywordsToRecipePaths.computeIfAbsent(keyword) { mutableListOf() }
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
        writeRecipesIndexFile(keywordsToRecipePaths.toSortedMap(), destination, agpVersion!!)
    }

    private fun writeRecipesIndexFile(
        keywordsToRecipePaths: MutableMap<String, MutableList<IndexData>>,
        destination: Path,
        agpVersion: String,
    ) {
        val builder = StringBuilder()
        val commaDelimiter = ", "
        builder.appendLine("""
            # Recipes for AGP version `$agpVersion`
            This branch contains recipes compatible with AGP $agpVersion. If you want to find recipes
            for other AGP versions, switch to the corresponding `agp-*` branch.

            This branch is read only. Contributions are only accepted on the `studio-main` branch. See `CONTRIBUTION.md`
            there.
        """.trimIndent())
        builder.appendLine("# Recipes Index")

        keywordsToRecipePaths.keys.forEach { indexKeyword ->
            builder.appendLine("* $indexKeyword - ")
            val joiner = StringJoiner(commaDelimiter)

            keywordsToRecipePaths[indexKeyword]?.forEach { data ->
                joiner.add("[${data.title}](${data.link})")
            }

            builder.appendLine(joiner.toString())
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
}