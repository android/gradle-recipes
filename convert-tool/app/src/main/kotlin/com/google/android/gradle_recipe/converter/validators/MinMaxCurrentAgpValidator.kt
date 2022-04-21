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
import com.google.android.gradle_recipe.converter.converters.agpToGradleVersions
import com.google.android.gradle_recipe.converter.recipe.RecipeMetadataParser
import com.google.android.gradle_recipe.converter.recipe.getAgpVersionMajorMinorFrom
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

/** Validates recipe from source mode and with currentAgpFileLocation, by calling validation
 *  with min and current/max AGP versions
 */
class MinMaxCurrentAgpValidator(private val currentAGPFileLocation: Path) {

    private val currentAGPVersionFile = "currentAgpVersion.txt"

    fun validate(source: Path) {
        val recipeMetadataParser = RecipeMetadataParser(source)
        val minAgpVersion = recipeMetadataParser.minAgpVersion
        val maxAgpVersion = recipeMetadataParser.maxAgpVersion

        validateRecipeFromSource(source, minAgpVersion)

        if (maxAgpVersion != null) {
            validateRecipeFromSource(source, maxAgpVersion)
        } else {
            var currentAgpVersion: String? = null
            val currentAgpFile = currentAGPFileLocation.resolve(currentAGPVersionFile).toFile()

            if (currentAgpFile.exists()) {
                currentAgpVersion = currentAgpFile.readText()
            }

            if (currentAgpVersion != null) {
                validateRecipeFromSource(source, currentAgpVersion)
            } else {
                error(
                    "Neither maxAgp version was defined in the metadata, " +
                            "nor currentAgp defined in $currentAGPVersionFile " +
                            "defined in $source ==> thus validated only with minAgp"
                )
            }
        }
    }

    private fun validateRecipeFromSource(
        from: Path,
        agpVersion: String,
    ) {
        val gradleVersion = agpToGradleVersions[getAgpVersionMajorMinorFrom(agpVersion)]

        val recipeConverter = RecipeConverter(
            agpVersion = agpVersion,
            gradleVersion = gradleVersion,
            repoLocation = null,
            gradlePath = null,
            mode = Mode.RELEASE,
            overwrite = true
        )

        val destinationFolder = createTempDirectory()
        destinationFolder.toFile().deleteOnExit()

        val conversionResult = recipeConverter.convert(
            source = from, destination = destinationFolder
        )

        if (conversionResult.isConversionSuccessful) {
            println("Validating: $destinationFolder with AGP: $agpVersion and Gradle: $gradleVersion")
            val tasksExecutor = GradleTasksExecutor(destinationFolder)
            tasksExecutor.executeTasks(conversionResult.recipe.tasks)
        }
    }
}