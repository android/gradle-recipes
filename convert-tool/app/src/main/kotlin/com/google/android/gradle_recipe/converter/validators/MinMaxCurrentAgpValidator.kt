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

package com.google.android.gradle_recipe.converter.validators

import com.google.android.gradle_recipe.converter.converters.RecipeConverter
import com.google.android.gradle_recipe.converter.converters.RecipeConverter.Mode
import com.google.android.gradle_recipe.converter.converters.getMaxAgp
import com.google.android.gradle_recipe.converter.converters.getVersionsFromAgp
import com.google.android.gradle_recipe.converter.printErrorAndTerminate
import com.google.android.gradle_recipe.converter.recipe.RecipeData
import com.google.android.gradle_recipe.converter.recipe.toMajorMinor
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.name

/**
 * Validates recipe from source mode and with currentAgpFileLocation, by calling validation
 *  with min and current/max AGP versions
 */
class MinMaxCurrentAgpValidator(
    private val branchRoot: Path,
) {

    private val maxAgp: String = getMaxAgp(branchRoot)

    fun validate(recipeFolder: Path, name: String? = null) {
        val finalName = name ?: recipeFolder.name
        val recipeDataMetadataParser = RecipeData.loadFrom(recipeFolder, Mode.RELEASE)

        validateRecipeFromSource(finalName, recipeFolder, recipeDataMetadataParser.minAgpVersion)
        validateRecipeFromSource(finalName, recipeFolder, recipeDataMetadataParser.maxAgpVersion ?: maxAgp)
    }

    private fun validateRecipeFromSource(
        name: String,
        from: Path,
        agpVersion: String,
    ) {
        val gradleVersion = getVersionsFromAgp(branchRoot, agpVersion.toMajorMinor())?.gradle
            ?: printErrorAndTerminate("Unable to find Gradle version for AGP version $agpVersion - Make sure it's present in version_mappings.txt")

        val recipeConverter = RecipeConverter(
            agpVersion = agpVersion,
            gradleVersion = gradleVersion,
            repoLocation = null,
            gradlePath = null,
            mode = Mode.RELEASE,
            branchRoot = branchRoot,
        )

        val destinationFolder = createTempDirectory().also { it.toFile().deleteOnExit() }

        val conversionResult = recipeConverter.convert(
            source = from, destination = destinationFolder
        )

        if (conversionResult.isConversionSuccessful) {
            println("Validating: Recipe $name ($destinationFolder) with AGP: $agpVersion and Gradle: $gradleVersion")
            val tasksExecutor = GradleTasksExecutor(destinationFolder)
            tasksExecutor.executeTasks(conversionResult.recipeData.tasks)
        }
    }
}