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
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.name

/** This will convert the recipe back to sources (using a temp folder),
 *  and then create 2 release versions, using the min and current/max versions of
 *  AGP and tests against these.
 */
class WorkingCopyValidator(
    private val branchRoot: Path,
) {

    fun validate(recipeSource: Path) {
        val recipeValidator = MinMaxCurrentAgpValidator(branchRoot)
        recipeValidator.validate(convertToSourceOfTruth(recipeSource), recipeSource.name)
    }

    private fun convertToSourceOfTruth(from: Path): Path {
        val sourceOfTruthTempDirectory: Path = createTempDirectory()
        sourceOfTruthTempDirectory.toFile().deleteOnExit()

        val convertToSourceTruth = RecipeConverter(
            agpVersion = null,
            gradleVersion = null,
            repoLocation = null,
            gradlePath = null,
            mode = Mode.SOURCE,
            overwrite = true,
            branchRoot = branchRoot,
        )
        convertToSourceTruth.convert(
            source = from, destination = sourceOfTruthTempDirectory
        )

        return sourceOfTruthTempDirectory
    }
}
