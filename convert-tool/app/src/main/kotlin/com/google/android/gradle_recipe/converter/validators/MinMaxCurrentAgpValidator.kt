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
import com.google.android.gradle_recipe.converter.converters.getGradleFromAgp
import com.google.android.gradle_recipe.converter.converters.getMaxAgp
import com.google.android.gradle_recipe.converter.recipe.RecipeMetadataParser
import com.google.android.gradle_recipe.converter.recipe.toMajorMinor
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.name

/** Validates recipe from source mode and with currentAgpFileLocation, by calling validation
 *  with min and current/max AGP versions
 */
class MinMaxCurrentAgpValidator(
    private val branchRoot: Path,
) {

    private val maxAgp: String = getMaxAgp(branchRoot)

    fun validate(recipeFolder: Path, name: String? = null) {
        val finalName = name ?: recipeFolder.name
        val recipeMetadataParser = RecipeMetadataParser(recipeFolder)

        validateRecipeFromSource(finalName, recipeFolder, recipeMetadataParser.minAgpVersion)
        validateRecipeFromSource(finalName, recipeFolder, recipeMetadataParser.maxAgpVersion ?: maxAgp)
    }

    private fun validateRecipeFromSource(
        name: String,
        from: Path,
        agpVersion: String,
    ) {
        val gradleVersion = getGradleFromAgp(branchRoot, agpVersion.toMajorMinor())
            ?: throw RuntimeException("Unable to find Gradle version for AGP version $agpVersion - Make sure it's present in version_mappings.txt")

        val recipeConverter = RecipeConverter(
            agpVersion = agpVersion,
            gradleVersion = gradleVersion,
            repoLocation = null,
            gradlePath = null,
            mode = Mode.RELEASE,
            overwrite = true,
            branchRoot = branchRoot,
        )

        val destinationFolder = createTempDirectory()
        destinationFolder.toFile().deleteOnExit()

        val conversionResult = recipeConverter.convert(
            source = from, destination = destinationFolder
        )

        if (conversionResult.isConversionSuccessful) {
            println("Validating: Recipe $name ($destinationFolder) with AGP: $agpVersion and Gradle: $gradleVersion")
            val tasksExecutor = GradleTasksExecutor(destinationFolder)
            tasksExecutor.executeTasks(conversionResult.recipe.tasks)
        }
    }
}