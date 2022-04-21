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

import com.google.android.gradle_recipe.converter.recipe.Recipe
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeLines

/**  This mode has the placeholders ($AGP_VERSION etc') and
 *   this is how we store the recipes in the dev branch
 */
class SourceConverter : Converter {
    override fun isConversionCompliant(recipe: Recipe): Boolean {
        return true
    }

    override fun convertSettingsGradle(source: Path, target: Path) {
        val originalLines = Files.readAllLines(source)
        val resultLines: List<String> = unwrapGradlePlaceholders(originalLines)

        target.writeLines(resultLines, Charsets.UTF_8)
    }

    override fun convertBuildGradle(source: Path, target: Path) {
        val originalLines = Files.readAllLines(source)
        val resultLines: List<String> = unwrapGradlePlaceholders(originalLines)

        target.writeLines(resultLines, Charsets.UTF_8)
    }

    override fun convertGradleWrapper(source: Path, target: Path) {
        val originalLines = Files.readAllLines(source)
        val resultLines: List<String> = unwrapGradleWrapperPlaceholders(originalLines)

        target.writeLines(resultLines, Charsets.UTF_8)
    }
}