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
import com.google.android.gradle_recipe.converter.recipe.RecipeData
import com.google.common.truth.Truth.assertThat
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.junit.Assert.fail
import org.junit.Test

class GradleRecipeTest {
    @Test
    fun run() {
        val name = System.getProperty("name")
        val source = Paths.get("tools/gradle-recipes/recipes/$name")
        val allTestedAgpVersions =
            System.getProperty("all_tested_agp_versions")?.split(",")
                ?: error("Missing required system property \"all_tested_agp_versions\".")
        val agpVersion =
            System.getProperty("agp_version")
                ?: error("Missing required system property \"agp_version\".")
        val gradlePath =
            System.getProperty("gradle_path")
                ?: error("Missing required system property \"gradle_path\".")
        checkVersionMappings(
            Paths.get("tools/gradle-recipes/version_mappings.txt").toFile(),
            allTestedAgpVersions,
            agpVersion,
            gradlePath
        )
        val destination = Paths.get(System.getenv("TEST_TMPDIR"), name, agpVersion)
        val outputDir = destination.resolve("out")

        // destination is not where the project will be created, so we need to compute this.
        // This is normally computed from the conversion itself, but the conversion need the
        // repo location which is provided by the Gradle instance which requires the project
        // location to be created.
        // So we need to manually load the RecipeData, even though the converter will do
        // this again later, in order to get the name of the destination folder (because
        // we are in RELEASE mode, this can be overridden, so it's not safe to hardcode the
        // logic.)
        val data = RecipeData.loadFrom(source, RELEASE)
        val destinationFolder = destination.resolve(data.destinationFolder)

        Gradle(destinationFolder.toFile(), outputDir.toFile(), File(gradlePath), null, false).use { gradle ->
            val repoPath = FileUtils.toSystemIndependentPath(gradle.repoDir.absolutePath)
            val recipeConverter =
                RecipeConverter(
                    agpVersion,
                    repoLocation = "maven { url = uri(\"$repoPath\") }",
                    gradleVersion = null,
                    gradlePath,
                    mode = RELEASE,
                    branchRoot = Paths.get("tools/gradle-recipes"),
                    generateWrapper = false,
                )
            val result = recipeConverter.convert(source, destination)

            val tasks = result.recipeData.tasks
            assertThat(tasks).isNotEmpty()

            val repos = System.getProperty("repos").split(",").map { File(it) }
            repos.forEach { gradle.addRepo(it) }
            gradle.addArgument("-Dcom.android.gradle.version=$agpVersion")
            gradle.addArgument("-Duser.home=${destination.resolve("tmp_home").toString()}")
            gradle.run(tasks)
        }
    }

    /**
     * Check that the list of all_tested_agp_versions fed to this test match the versions specified
     * in version_mappings.txt.
     *
     * Also check that the agp_version and gradle_version fed to this test match the corresponding
     * line in version_mappings.txt.
     *
     * These checks are important because the version_mappings.txt file dictates which versions are
     * used on github.
     */
    private fun checkVersionMappings(
        versionMappingsFile: File,
        allTestedAgpVersions: List<String>,
        agpVersion: String,
        gradlePath: String
    ) {
        // Read the version_mappings.txt file and create lists of expected AGP and Gradle versions
        val expectedAgpVersions = mutableListOf<String>()
        val expectedGradleVersions = mutableListOf<String>()
        versionMappingsFile.forEachLine { line ->
            if (!line.startsWith("#")) {
                val versionList = line.split(";")
                expectedAgpVersions.add(versionList[0])
                expectedGradleVersions.add(versionList[1])
            }
        }

        // Check that all versions from allTestedAgpVersions (except "ToT") are represented in the
        // version_mappings.txt file (i.e., the file has an AGP version with matching major and
        // minor versions).
        allTestedAgpVersions.forEachIndexed { i, testedAgpVersion ->
            if (testedAgpVersion != "ToT") {
                assertThat(testedAgpVersion.take(4)).isEqualTo(expectedAgpVersions[i].take(4))
            }
        }

        // Check that agpVersion is represented in the version_mappings.txt file with a Gradle
        // version matching gradlePath.
        var found = false
        expectedAgpVersions.forEachIndexed { i, expectedAgpVersion ->
            if (agpVersion.take(4) == expectedAgpVersion.take(4)) {
                found = true
                assertThat(gradlePath).contains(expectedGradleVersions[i])
            }
        }
        if (!found) {
            fail("AGP Version $agpVersion not found in version_mappings.txt.")
        }
    }
}