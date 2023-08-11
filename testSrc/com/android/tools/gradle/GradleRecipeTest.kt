/*
 * Copyright 2023 Google, Inc.
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

package com.android.tools.gradle

import com.android.tools.gradle.Gradle
import com.android.utils.FileUtils
import com.google.android.gradle_recipe.converter.converters.RecipeConverter
import com.google.android.gradle_recipe.converter.converters.RecipeConverter.Mode.RELEASE
import com.google.android.gradle_recipe.converter.recipe.RecipeMetadataParser
import com.google.common.truth.Truth.assertThat
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.Test

class GradleRecipeTest {
    @Test
    fun run() {
        val name = System.getProperty("name")
        val source = Paths.get("tools/gradle-recipes/recipes/$name")
        val testedAgpVersions = System.getProperty("tested_agp_versions")?.split(",")
        val testedGradlePaths = System.getProperty("tested_gradle_paths")?.split(",")
        if (testedAgpVersions != null && testedGradlePaths != null) {
            assertThat(testedAgpVersions.size).isEqualTo(testedGradlePaths.size)
            checkVersionMappings(
                Paths.get("tools/gradle-recipes/version_mappings.txt").toFile(),
                testedAgpVersions,
                testedGradlePaths
            )
        }
        val agpVersion = System.getProperty("agp_version")
        val gradlePath = System.getProperty("gradle_path")
        val destination = Paths.get(System.getenv("TEST_TMPDIR"), name, agpVersion)
        val outputDir = destination.resolve("out")
        Gradle(destination.toFile(), outputDir.toFile(), File(gradlePath), null, false).use { gradle ->
            val repoPath = FileUtils.toSystemIndependentPath(gradle.repoDir.absolutePath)
            val recipeConverter =
                RecipeConverter(
                    agpVersion,
                    repoLocation = "maven { url = uri(\"$repoPath\") }",
                    gradleVersion = null,
                    gradlePath,
                    mode = RELEASE,
                    overwrite = true
                )
            recipeConverter.convert(source, destination)

            val tasks = RecipeMetadataParser(destination).tasks
            assertThat(tasks).isNotEmpty()

            val repos = System.getProperty("repos").split(",").map { File(it) }
            repos.forEach { gradle.addRepo(it) }
            gradle.addArgument("-Dcom.android.gradle.version=$agpVersion")
            gradle.addArgument("-Duser.home=${destination.resolve("tmp_home").toString()}")
            gradle.run(tasks)
        }
    }

    /**
     * Check that the tested_agp_versions and tested_gradle_paths properties fed to this test are
     * in sync with the versions specified in version_mappings.txt.
     */
    private fun checkVersionMappings(
        versionMappingsFile: File,
        agpVersions: List<String>,
        gradlePaths: List<String>
    ) {
        val expectedAgpVersions = mutableListOf<String>()
        val expectedGradleVersions = mutableListOf<String>()
        versionMappingsFile.forEachLine { line ->
            if (!line.startsWith("#")) {
                val versionList = line.split(";")
                expectedAgpVersions.add(versionList[0])
                expectedGradleVersions.add(versionList[1])
            }
        }
        assertThat(agpVersions.size).isEqualTo(expectedAgpVersions.size)
        // Don't check the last AGP version because the ToT AGP version might be different than the
        // version in the version_mappings.txt file.
        agpVersions.dropLast(1).forEachIndexed { i, agpVersion ->
            assertThat(agpVersion.take(4)).isEqualTo(expectedAgpVersions[i].take(4))
        }
        assertThat(gradlePaths.size).isEqualTo(expectedGradleVersions.size)
        // Don't check the last Gradle path because the ToT Gradle version might be different than
        // the version in the version_mappings.txt file.
        gradlePaths.dropLast(1).forEachIndexed { i, gradlePath ->
            assertThat(gradlePath).contains(expectedGradleVersions[i])
        }
    }
}