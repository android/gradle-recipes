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
import java.nio.file.Path

/** Interface for different converters.
 *  The objects are created and called from the RecipeConverter class,
 *  using a Template Method pattern.
 */
interface Converter {

    /** Can converter convert this recipe
     */
    fun isConversionCompliant(recipe: Recipe): Boolean

    /** Sets the recipe to convert, before the conversion
     */
    fun setRecipe(recipe: Recipe) {

    }

    /**
     *  Converts settings.gradle
     */
    fun convertSettingsGradle(source: Path, target: Path)

    /**
     *  Converts settings.gradle.kts ==> same as settings.gradle
     */
    fun convertSettingsGradleKts(source: Path, target: Path) {
        convertSettingsGradle(source, target)
    }

    /**
     * Converts build.gradle
     */
    fun convertBuildGradle(source: Path, target: Path)

    /**
     *  Converts build.gradle.kts ==> same as build.gradle
     */
    fun convertBuildGradleKts(source: Path, target: Path) {
        convertBuildGradle(source, target)
    }

    /**
     * Converts gradle.wrapper
     */
    fun convertGradleWrapper(source: Path, target: Path)
}