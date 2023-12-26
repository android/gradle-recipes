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

import com.google.android.gradle_recipe.converter.recipe.RecipeData
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeLines

/**
 * This is the working copy where the recipe has static values for $AGP_VERSION, etc...
 * but markers to revert them back to placeholders.
 */
class WorkingCopyConverter(branchRoot: Path) : Converter(branchRoot) {

    override fun isConversionCompliant(recipeData: RecipeData): Boolean {
        return true
    }

    override fun convertBuildGradle(source: Path, target: Path) {
        val agpVersion = getMinAgp()

        val convertedText = Files.readAllLines(source)
            .wrapGradlePlaceholdersWithInlineValue(
                "\$AGP_VERSION",
                "\"$agpVersion\""
            ).wrapGradlePlaceholdersWithInlineValue(
                "\$KOTLIN_VERSION",
                "\"${getVersionInfoFromAgp(agpVersion).kotlin}\""
            ).wrapGradlePlaceholdersWithInlineValue(
                "\$COMPILE_SDK",
                compileSdkVersion
            ).wrapGradlePlaceholdersWithInlineValue(
                "\$MINIMUM_SDK",
                minimumSdkVersion
            )

        target.writeLines(convertedText, Charsets.UTF_8)
    }

    override fun convertSettingsGradle(source: Path, target: Path) {
        val convertedText = Files.readAllLines(source)
            .wrapGradlePlaceholdersWithInlineValue(
                "\$AGP_REPOSITORY",
                ""
            ).wrapGradlePlaceholdersWithList(
                "\$PLUGIN_REPOSITORIES",
                listOf(
                    "        gradlePluginPortal()",
                    "        google()",
                    "        mavenCentral()"
                )
            ).wrapGradlePlaceholdersWithList(
                "\$DEPENDENCY_REPOSITORIES",
                listOf("        google()", "        mavenCentral()")
            )

        target.writeLines(convertedText, Charsets.UTF_8)
    }

    override fun convertVersionCatalog(source: Path, target: Path) {
        val agpVersion = getMinAgp()

        val convertedText = Files.readAllLines(source)
            .wrapVersionCatalogPlaceholders(
                "\$AGP_VERSION",
                "\"$agpVersion\""
            ).wrapVersionCatalogPlaceholders(
                "\$KOTLIN_VERSION",
                "\"${getVersionInfoFromAgp(agpVersion).kotlin}\""
            )

        target.writeLines(convertedText, Charsets.UTF_8)
    }

    override fun processGradleWrapperProperties(file: Path) {
        // building the line
        // distributionUrl=https\://services.gradle.org/distributions/gradle-7.2-bin.zip
        file.writeLines(
            Files.readAllLines(file)
                .wrapGradleWrapperPlaceholders(
                    "\$GRADLE_LOCATION",
                    "https\\://services.gradle.org/distributions/gradle-${getVersionInfoFromAgp(getMinAgp()).gradle}-bin.zip"
                ),
            Charsets.UTF_8
        )
    }
}