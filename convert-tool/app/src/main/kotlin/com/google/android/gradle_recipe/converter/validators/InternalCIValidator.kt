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
import com.google.android.gradle_recipe.converter.recipe.visitRecipes
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

/** Validates al the recipes against a very specific version
 *  of AGP, and Gradle, in specific locations
 */
class InternalCIValidator(
    private val agpVersion: String,
    private val repoLocation: String,
    private val gradlePath: String,
    private val branchRoot: Path,
) {
    @Throws(IOException::class)
    fun validate(sourceAll: Path, tmpFolder: Path?) {

        val converter = RecipeConverter(
            agpVersion = agpVersion,
            repoLocation = repoLocation,
            gradleVersion = null,
            gradlePath = gradlePath,
            mode = Mode.RELEASE,
            overwrite = true,
            branchRoot = branchRoot,
        )

        visitRecipes(sourceAll) { recipeFolder: Path ->
            val destinationFolder: Path

            if (tmpFolder != null) {
                destinationFolder = tmpFolder
            } else {
                destinationFolder = createTempDirectory()
                destinationFolder.toFile().deleteOnExit()
            }

            val conversionResult = converter.convert(
                source = recipeFolder, destination = destinationFolder
            )

            if (conversionResult.isConversionSuccessful) {
                println("Validating: $destinationFolder with AGP: $agpVersion and Gradle: $gradlePath")
                val tasksExecutor = GradleTasksExecutor(destinationFolder)
                tasksExecutor.executeTasks(conversionResult.recipeData.tasks)
            }
        }
    }
}
