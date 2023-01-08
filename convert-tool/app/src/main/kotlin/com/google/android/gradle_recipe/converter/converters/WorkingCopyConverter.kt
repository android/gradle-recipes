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
import com.google.android.gradle_recipe.converter.recipe.getAgpVersionMajorMinorFrom
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeLines

/**
 * This is the working copy where the recipe has static values for $AGP_VERSION, etc...
 * but markers to revert them back to placeholders.
 */
class WorkingCopyConverter : Converter {
    private var recipe: Recipe? = null
    override fun isConversionCompliant(recipe: Recipe): Boolean {
        return true
    }

    override fun setRecipe(recipe: Recipe) {
        this.recipe = recipe
    }

    override fun convertBuildGradle(source: Path, target: Path) {
        val agpVersion = recipe?.minAgpVersion ?: error("min Agp version is badly specified in the metadata")

        val originalLines = Files.readAllLines(source)
        val agpVersionWrapped = wrapGradlePlaceholdersWithInlineValue(
            originalLines, "\$AGP_VERSION", "\"$agpVersion\""
        )
        val kotlinAndAgpVersionWrapped =
            wrapGradlePlaceholdersWithInlineValue(
                agpVersionWrapped,
                "\$KOTLIN_VERSION",
                "\"$kotlinPluginVersion\""
            )

        target.writeLines(kotlinAndAgpVersionWrapped, Charsets.UTF_8)
    }

    override fun convertSettingsGradle(source: Path, target: Path) {
        val originalLines = Files.readAllLines(source)

        val agpConverted = wrapGradlePlaceholdersWithInlineValue(originalLines, "\$AGP_REPOSITORY", "")
        val agpAndPluginRepoConverted = wrapGradlePlaceholdersWithList(
            agpConverted, "\$PLUGIN_REPOSITORIES",
            listOf("        gradlePluginPortal()", "        google()", "        mavenCentral()")
        )

        val agpAndPluginRepoAndDepsRepoConverted = wrapGradlePlaceholdersWithList(
            agpAndPluginRepoConverted, "\$DEPENDENCY_REPOSITORIES",
            listOf("        google()", "        mavenCentral()")
        )

        target.writeLines(agpAndPluginRepoAndDepsRepoConverted, Charsets.UTF_8)
    }

    override fun convertVersionCatalog(source: Path, target: Path) {
        val agpVersion = recipe?.minAgpVersion ?: error("min Agp version is badly specified in the metadata")
        val originalLines = Files.readAllLines(source)

        val agpVersionWrapped = wrapVersionCatalogPlaceholders(
            originalLines,
            "\$AGP_VERSION",
            "\"$agpVersion\""
        )

        val kotlinAndAgpVersionWrapped =
            wrapVersionCatalogPlaceholders(
                agpVersionWrapped,
                "\$KOTLIN_VERSION",
                "\"$kotlinPluginVersion\""
            )

        target.writeLines(kotlinAndAgpVersionWrapped, Charsets.UTF_8)
    }

    override fun copyGradleFolder(dest: Path) {
        val source = Path.of(System.getProperty("user.dir")).resolve(GRADLE_RESOURCES_FOLDER)
        source.toFile().copyRecursively(
            target = dest.toFile(),
            overwrite = true,
            onError = { _: File, _: IOException ->
                println("Could not create the gradle folder, please create it manually")
                OnErrorAction.SKIP
            }
        )

        convertGradleWrapper(
            dest.resolve("gradle").resolve("wrapper").resolve("gradle-wrapper.properties"),
            dest.resolve("gradle").resolve("wrapper").resolve("gradle-wrapper.properties")
        )
    }

    private fun convertGradleWrapper(source: Path, target: Path) {
        // building the line
        // distributionUrl=https\://services.gradle.org/distributions/gradle-7.2-bin.zip
        val agpVersion = recipe?.minAgpVersion ?: error("min Agp version is badly specified in the metadata")

        val agpVersionMajorMinor = getAgpVersionMajorMinorFrom(agpVersion)
        val gradleVersion = agpToGradleVersions[agpVersionMajorMinor]
            ?: error("Can't deduce the gradle version from the recipe metadata")

        val originalLines = Files.readAllLines(source)
        val resultLines = wrapGradleWrapperPlaceholders(
            originalLines,
            "\$GRADLE_LOCATION",
            "https\\://services.gradle.org/distributions/gradle-$gradleVersion-bin.zip"
        )
        target.writeLines(resultLines, Charsets.UTF_8)
    }
}