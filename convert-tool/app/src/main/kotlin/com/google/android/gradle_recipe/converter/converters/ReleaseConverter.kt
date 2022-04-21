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

/** This mode is for a recipe that has no placeholders.
 *  This is for releases ando/or tests
 */
class ReleaseConverter(
    private val agpVersion: String,
    gradleVersion: String?,
    repoLocation: String?,
    gradlePath: String?,
) : Converter {

    private var pathToGradle: String = ""
    private var pathToRepo: String = ""

    init {

        if (gradleVersion != null) {
            pathToGradle = "https\\://services.gradle.org/distributions/gradle-" +
                    "$gradleVersion-bin.zip"
        } else {
            pathToRepo = repoLocation ?: error("must specify path to repo")
            pathToGradle = gradlePath ?: error("must specify path to Gradle")
        }
    }

    override fun isConversionCompliant(recipe: Recipe): Boolean {
        return recipe.isCompliantWithAgp(agpVersion)
    }

    override fun convertBuildGradle(source: Path, target: Path) {
        val originalLines = Files.readAllLines(source)
        val resultLines: List<String> = replacePlaceHolderWithValue(
            originalLines,
            "\$AGP_VERSION",
            "\"$agpVersion\""
        )

        target.writeLines(resultLines, Charsets.UTF_8)
    }

    override fun convertSettingsGradle(source: Path, target: Path) {
        val originalLines = Files.readAllLines(source)
        val resultLines: List<String> = replacePlaceHolderWithValue(
            originalLines, "\$AGP_REPOSITORY", "$pathToRepo"
        )

        target.writeLines(resultLines, Charsets.UTF_8)
    }

    override fun convertGradleWrapper(source: Path, target: Path) {
        val originalLines = Files.readAllLines(source)
        val resultLines: List<String> = replacePlaceHolderWithValue(
            originalLines, "\$GRADLE_LOCATION", "$pathToGradle"
        )

        target.writeLines(resultLines, Charsets.UTF_8)
    }
}