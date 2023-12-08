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
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.writeLines

/** This mode is for a recipe that has no placeholders.
 *  This is for releases ando/or tests
 */
class ReleaseConverter(
    private val agpVersion: String,
    gradleVersion: String?,
    repoLocation: String?,
    gradlePath: String?,
    private val branchRoot: Path,
) : Converter {

    private var pathToGradle: String = ""
    private var pathToAgpRepo: String = ""

    private var pluginRepo: List<String> = listOf()
    private var dependencyRepo: List<String> = listOf()

    init {
        if (gradleVersion != null) {
            // github release
            pathToGradle = "https\\://services.gradle.org/distributions/gradle-" +
                    "$gradleVersion-bin.zip"
            pluginRepo = listOf("        gradlePluginPortal()", "        google()", "        mavenCentral()")
            dependencyRepo = listOf("        google()", "        mavenCentral()")

        } else {
            // internal CI release
            pathToAgpRepo = repoLocation ?: error("must specify path to repo")
            pathToGradle = gradlePath ?: error("must specify path to Gradle")
        }
    }

    override fun isConversionCompliant(recipe: Recipe): Boolean {
        return recipe.isCompliantWithAgp(agpVersion)
    }

    override fun convertBuildGradle(source: Path, target: Path) {
        val originalLines = Files.readAllLines(source)
        val agpVersionReplaced = replaceGradlePlaceholdersWithInlineValue(
            originalLines,
            "\$AGP_VERSION",
            "\"$agpVersion\""
        )

        val kotlinAndAgpVersionReplaced =
            replaceGradlePlaceholdersWithInlineValue(
                agpVersionReplaced,
                "\$KOTLIN_VERSION",
                "\"$kotlinPluginVersion\""
            )

        val kotlinAndAgpCompileSdkVersionReplaced =
            replaceGradlePlaceholdersWithInlineValue(
                kotlinAndAgpVersionReplaced,
                "\$COMPILE_SDK",
                "$compileSdkVersion"
            )

        val kotlinAndAgpCompileSdkMinimumSdkVersionReplaced =
            replaceGradlePlaceholdersWithInlineValue(
                kotlinAndAgpCompileSdkVersionReplaced,
                "\$MINIMUM_SDK",
                "$minimumSdkVersion"
            )

        target.writeLines(kotlinAndAgpCompileSdkMinimumSdkVersionReplaced, Charsets.UTF_8)
    }

    override fun convertSettingsGradle(source: Path, target: Path) {
        val originalLines = Files.readAllLines(source)

        val agpRepoConverted =
            replacePlaceHolderWithLine(
                originalLines,
                "\$AGP_REPOSITORY",
                "$pathToAgpRepo"
            )

        val agpAndPluginRepoConverted = replacePlaceHolderWithList(
            agpRepoConverted, "\$PLUGIN_REPOSITORIES",
            pluginRepo
        )

        val agpAndPluginAndDependencyRepoConverted = replacePlaceHolderWithList(
            agpAndPluginRepoConverted, "\$DEPENDENCY_REPOSITORIES",
            dependencyRepo
        )

        target.writeLines(agpAndPluginAndDependencyRepoConverted, Charsets.UTF_8)
    }

    override fun convertVersionCatalog(source: Path, target: Path) {
        val originalLines = Files.readAllLines(source)

        val agpVersionReplaced = replaceVersionCatalogPlaceholders(
            originalLines,
            "\$AGP_VERSION",
            "\"$agpVersion\""
        )

        val kotlinAndAgpVersionReplaced =
            replaceGradlePlaceholdersWithInlineValue(
                agpVersionReplaced,
                "\$KOTLIN_VERSION",
                "\"$kotlinPluginVersion\""
            )
        target.writeLines(kotlinAndAgpVersionReplaced, Charsets.UTF_8)
    }

    override fun copyGradleFolder(dest: Path) {
        val source = branchRoot.resolve(GRADLE_RESOURCES_FOLDER)
        if (!source.isDirectory()) {
            throw RuntimeException("Unable to find gradle resources at $source")
        }

        dest.mkdirs()

        source.toFile().copyRecursively(target = dest.toFile())

        val gradleWrapperPropertiesPath =
            dest.resolve("gradle").resolve("wrapper").resolve("gradle-wrapper.properties")
        if (Files.exists(gradleWrapperPropertiesPath)) {
            convertGradleWrapper(gradleWrapperPropertiesPath, gradleWrapperPropertiesPath)
        }
    }

    private fun convertGradleWrapper(source: Path, target: Path) {
        val originalLines = Files.readAllLines(source)
        val resultLines = replaceGradlePlaceholdersWithInlineValue(
            originalLines, "\$GRADLE_LOCATION", "$pathToGradle"
        )

        target.writeLines(resultLines, Charsets.UTF_8)
    }
}