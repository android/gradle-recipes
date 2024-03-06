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
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.name

/**
 * This will convert the recipe back to sources (using a temp folder) and then validate it.
 *
 * If [agpVersion] is not null, that version is used for validation. Otherwise, the recipe is
 * validated using both the min and the current/max AGP versions.
 */
class WorkingCopyValidator(
    private val context: Context,
    private val agpVersion: FullAgpVersion? = null,
) {

    fun validate(recipeSource: Path) {
        val recipeValidator = SourceValidator(context, agpVersion)
        recipeValidator.validate(convertToSourceOfTruth(recipeSource), recipeSource.name)
    }

    private fun convertToSourceOfTruth(from: Path): Path {
        val destination: Path = createTempDirectory().also { it.toFile().deleteOnExit() }

        val convertToSourceTruth = RecipeConverter(
            context = context,
            agpVersion = null,
            gradleVersion = null,
            mode = Mode.SOURCE,
        )
        val result = convertToSourceTruth.convert(source = from, destination = destination)

        return destination.resolve(result.recipeData.destinationFolder)
    }
}
