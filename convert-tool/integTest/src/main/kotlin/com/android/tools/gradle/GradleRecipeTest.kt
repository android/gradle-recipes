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
import com.android.testutils.TestUtils
import com.google.android.gradle_recipe.converter.convertAndValidate
import com.google.android.gradle_recipe.converter.context.DefaultContext
import com.google.android.gradle_recipe.converter.context.Context
import com.google.android.gradle_recipe.converter.converters.FullAgpVersion
import com.google.android.gradle_recipe.converter.converters.ResultMode
import com.google.common.truth.Truth.assertWithMessage
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import org.junit.Assume
import org.junit.Test

class GradleRecipeTest {

    /**
     * This test uses commands similar to those found in CONTRIBUTING.md in order to provide test
     * coverage for the developer workflow described there.
     */
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
        val jdkVersion = System.getProperty("jdk_version")
        val validateSource =
            System.getProperty("validate_source")?.toBooleanStrict()
                ?: error("Missing required system property \"validate_source\".")


        val context = DefaultContext.createDefaultContext(Paths.get("tools/gradle-recipes"))
        val fullAgpVersion = FullAgpVersion.of(agpVersion)

        checkVersionMappings(
            context,
            allTestedAgpVersions,
            fullAgpVersion,
        )
        val tempDir = Paths.get(System.getenv("TEST_TMPDIR"), name, agpVersion)
        val repoDir = tempDir.resolve("repo").toFile()
        val workingCopyDir = tempDir.resolve("working_copy").toFile()
        FileUtils.mkdirs(repoDir)
        FileUtils.mkdirs(workingCopyDir)

        // Create local maven repo
        val repos = System.getProperty("repos").split(",").map { File(it) }
        repos.forEach { Gradle.addRepo(it, repoDir) }

        val result = convertAndValidate(
            arrayOf(
                "validate",
                "--mode",
                "source",
                "--source",
                source.toFile().absolutePath,
                "--gradleRecipesFolder",
                Paths.get("tools/gradle-recipes").toFile().absolutePath,
                "--agpVersion",
                agpVersion,
                "--repoLocation",
                FileUtils.toSystemIndependentPath(repoDir.absolutePath),
                "--gradlePath",
                File(gradlePath).toURI().toString(),
                "--javaHome",
                getJDKPath(jdkVersion).toFile().absolutePath,
                "--androidHome",
                File(TestUtils.getRelativeSdk()).absolutePath,
                "--ci"
            )
        )

        // if the recipe was skipped, then mark the test as ignored.
        Assume.assumeFalse(result == ResultMode.SKIPPED)
    }

    private fun getJDKPath(jdkVersion: String?): Path {
        return when(jdkVersion) {
            null, "17" -> TestUtils.getJava17Jdk()
            else -> throw IllegalArgumentException("Unsupported jdk_version: $jdkVersion. Only JDK 17 is supported.")
        }
    }

    /**
     * Check that all AGP versions fed to this test (all_tested_agp_versions and agp_version) have
     * an entry for their major.minor version in version_mappings.txt.
     *
     * Only the AGP version is checked. The Gradle version used for validation comes from
     * gradle_path, so the Gradle column in version_mappings.txt is not compared against it.
     *
     * These checks are important because the version_mappings.txt file dictates which versions are
     * published on github.
     */
    private fun checkVersionMappings(
        context: Context,
        allTestedAgpVersions: List<String>,
        agpVersion: FullAgpVersion,
    ) {
        val keys = context.versionMappings.keys

        val expectedVersions =
            allTestedAgpVersions.filter { it != "ToT" }.map { FullAgpVersion.of(it).toShort() } +
                agpVersion.toShort()
        expectedVersions.distinct().forEach { shortVersion ->
            assertWithMessage(
                "AGP $shortVersion is missing from version_mappings.txt. Add a " +
                    "\"$shortVersion;<gradle_version>;<kotlin_version>\" line to " +
                    "tools/gradle-recipes/version_mappings.txt."
            ).that(keys).contains(shortVersion)
        }
    }
}
