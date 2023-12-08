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

package com.google.android.gradle_recipe.converter.recipe

import com.github.rising3.semver.SemVer
import java.io.File
import java.nio.file.Path

/**
 * A recipe metadata model
 */
class Recipe(
    val minAgpVersion: String,
    val maxAgpVersion: String?,
    val tasks: List<String>,
    val keywords: List<String>,
) {
    fun isCompliantWithAgp(agpVersion: String): Boolean {
        val min = minAgpVersion
        val max = maxAgpVersion

        return if (max != null) {
            SemVer.parse(agpVersion) >= SemVer.parse(min) && SemVer.parse(agpVersion) <= SemVer.parse(max)
        } else {
            // when maxAgpVersion is not specified
            SemVer.parse(agpVersion) >= SemVer.parse(min)
        }
    }
}

fun String.toMajorMinor(): String = SemVer.parse(this).run { "${this.major}.${this.minor}" }

fun isRecipeFolder(folder: Path) = File(folder.toFile(), RECIPE_METADATA_FILE).exists()
