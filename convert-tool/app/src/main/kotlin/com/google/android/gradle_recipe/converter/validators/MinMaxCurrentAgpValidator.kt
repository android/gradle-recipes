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

import com.google.android.gradle_recipe.converter.context.Context
import com.google.android.gradle_recipe.converter.converters.FullAgpVersion
import com.google.android.gradle_recipe.converter.converters.RecipeConverter
import com.google.android.gradle_recipe.converter.converters.RecipeConverter.Mode
import com.google.android.gradle_recipe.converter.converters.ResultMode
import com.google.android.gradle_recipe.converter.printErrorAndTerminate
import com.google.android.gradle_recipe.converter.recipe.RecipeData
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.name

/**
 * Validates recipe from source mode and with currentAgpFileLocation, by calling validation
 *  with min and current/max AGP versions
 */
class MinMaxCurrentAgpValidator(private val context: Context) {

    fun validate(recipeFolder: Path, name: String? = null) {
        val finalName = name ?: recipeFolder.name
        val recipeData = RecipeData.loadFrom(recipeFolder, Mode.RELEASE, context)

        validateRecipeFromSource(finalName, recipeFolder, recipeData.minAgpVersion)

        val max = if (recipeData.maxAgpVersion == null) {
            context.maxPublishedAgp
        } else {
            context.getPublishedAgp(recipeData.maxAgpVersion)
        }

        validateRecipeFromSource(finalName, recipeFolder, max)
    }

    private fun validateRecipeFromSource(
        name: String,
        from: Path,
        agpVersion: FullAgpVersion,
    ) {
        val gradleVersion = context.getGradleVersion(agpVersion.toShort())

        val recipeConverter = RecipeConverter(
            context = context,
            agpVersion = agpVersion,
            gradleVersion = gradleVersion,
            repoLocation = null,
            gradlePath = null,
            mode = Mode.RELEASE,
        )

        val destinationFolder = createTempDirectory().also { it.toFile().deleteOnExit() }

        val conversionResult = recipeConverter.convert(
            source = from, destination = destinationFolder
        )

        // the recipe destination is inside destinationFolder
        val recipeFolder = destinationFolder.resolve(conversionResult.recipeData.destinationFolder)

        if (conversionResult.resultMode == ResultMode.SUCCESS) {
            println("Validating: Recipe $name ($recipeFolder) with AGP: $agpVersion and Gradle: $gradleVersion")
            val tasksExecutor = GradleTasksExecutor(recipeFolder)
            tasksExecutor.executeTasks(conversionResult.recipeData.tasks)

            if (conversionResult.recipeData.validationTasks != null) {
                tasksExecutor.executeTasks(conversionResult.recipeData.validationTasks)
            }
        }
    }
}