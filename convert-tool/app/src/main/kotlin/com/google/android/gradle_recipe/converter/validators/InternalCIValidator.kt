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

import com.google.android.gradle_recipe.converter.converters.FullAgpVersion
import com.google.android.gradle_recipe.converter.converters.RecipeConverter
import com.google.android.gradle_recipe.converter.converters.RecipeConverter.Mode
import com.google.android.gradle_recipe.converter.converters.ResultMode
import com.google.android.gradle_recipe.converter.recipe.visitRecipes
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

/** Validates al the recipes against a very specific version
 *  of AGP, and Gradle, in specific locations
 */
class InternalCIValidator(
    private val agpVersion: FullAgpVersion,
    private val repoLocation: String,
    private val gradlePath: String,
) {
    fun validate(sourceAll: Path, tmpFolder: Path?) {

        val converter = RecipeConverter(
            agpVersion = agpVersion,
            repoLocation = repoLocation,
            gradleVersion = null,
            gradlePath = gradlePath,
            mode = Mode.RELEASE,
        )

        val destinationFolder = tmpFolder ?: createTempDirectory().also {
            it.toFile().deleteOnExit()
        }

        visitRecipes(sourceAll) { recipeFolder: Path ->

            val conversionResult = converter.convert(
                source = recipeFolder, destination = destinationFolder
            )

            if (conversionResult.resultMode == ResultMode.SUCCESS) {
                println("Validating: $destinationFolder with AGP: $agpVersion and Gradle: $gradlePath")
                val tasksExecutor = GradleTasksExecutor(destinationFolder)
                tasksExecutor.executeTasks(conversionResult.recipeData.tasks)

                if (conversionResult.recipeData.validationTasks != null) {
                    tasksExecutor.executeTasks(conversionResult.recipeData.validationTasks)
                }
            }
        }
    }
}
