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
import com.google.android.gradle_recipe.converter.main
import com.google.android.gradle_recipe.converter.context.DefaultContext
import com.google.android.gradle_recipe.converter.context.Context
import com.google.android.gradle_recipe.converter.converters.FullAgpVersion
import com.google.android.gradle_recipe.converter.converters.ShortAgpVersion
import com.google.common.truth.Truth.assertThat
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import org.junit.Assert.fail
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

        val context = DefaultContext.createDefaultContext(Paths.get("tools/gradle-recipes"))
        val fullAgpVersion = FullAgpVersion.of(agpVersion)

        checkVersionMappings(
            context,
            allTestedAgpVersions,
            fullAgpVersion,
            gradlePath
        )
        val tempDir = Paths.get(System.getenv("TEST_TMPDIR"), name, agpVersion)
        val repoDir = tempDir.resolve("repo").toFile()
        val workingCopyDir = tempDir.resolve("working_copy").toFile()
        FileUtils.mkdirs(repoDir)
        FileUtils.mkdirs(workingCopyDir)

        // Create local maven repo
        val repos = System.getProperty("repos").split(",").map { File(it) }
        repos.forEach { Gradle.addRepo(it, repoDir) }

        // Convert to working copy
        main(
            arrayOf(
                "convert",
                "--mode",
                "workingcopy",
                "--source",
                source.toFile().absolutePath,
                "--destination",
                workingCopyDir.absolutePath,
                "--gradleRecipesFolder",
                Paths.get("tools/gradle-recipes").toFile().absolutePath,
                "--agpVersion",
                agpVersion,
                "--repoLocation",
                FileUtils.toSystemIndependentPath(repoDir.absolutePath),
                "--gradlePath",
                File(gradlePath).toURI().toString()
            )
        )

        // Validate working copy
        main(
            arrayOf(
                "validate",
                "--mode",
                "workingcopy",
                "--source",
                File(workingCopyDir, name).absolutePath,
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
    }

    private fun getJDKPath(jdkVersion: String?): Path {
        return when(jdkVersion) {
            "8" -> TestUtils.getJava8Jdk()
            "11" -> TestUtils.getJava11Jdk()
            "17" -> TestUtils.getJava17Jdk()
            else -> TestUtils.getJava17Jdk()
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
        context: Context,
        allTestedAgpVersions: List<String>,
        agpVersion: FullAgpVersion,
        gradlePath: String
    ) {
        val versionMappings: Map<ShortAgpVersion, Context.VersionInfo> = context.versionMappings
        val keys = versionMappings.keys

        // Check that all versions from allTestedAgpVersions (except "ToT") are represented in the
        // version_mappings.txt file (i.e., the file has an AGP version with matching major and
        // minor versions).
        allTestedAgpVersions.forEachIndexed { i, testedAgpVersion ->
            if (testedAgpVersion != "ToT") {
                val shortVersion = FullAgpVersion.of(testedAgpVersion).toShort()
                assertThat(keys).contains(shortVersion)
            }
        }

        // Check that agpVersion is represented in the version_mappings.txt file with a Gradle
        // version matching gradlePath.
        context.getGradleVersion(agpVersion)?.let {
            assertThat(gradlePath).contains(context.getGradleVersion(agpVersion))
        } ?: fail("AGP Version $agpVersion not found in version_mappings.txt.")
    }
}
