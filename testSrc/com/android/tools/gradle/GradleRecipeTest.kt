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
        val destination = Paths.get(System.getenv("TEST_TMPDIR"), name)
        val agpVersion = System.getProperty("agp_version")
        val gradlePath = System.getProperty("gradle_path")
        val repos = System.getProperty("repos").split(",").map { File(it) }
        val recipeConverter =
            RecipeConverter(
                agpVersion,
                // Add multiple repo locations because build files are at different levels in the
                // project
                repoLocation = """
                    maven { url = uri("./out/_repo") }
                    maven { url = uri("../out/_repo") }
                """.trimIndent(),
                gradleVersion = null,
                gradlePath,
                mode = RELEASE,
                overwrite = true
            )
        recipeConverter.convert(source, destination)

        val tasks = RecipeMetadataParser(destination).tasks
        assertThat(tasks).isNotEmpty()

        val outputDir = destination.resolve("out")
        val home = destination.resolve("tmp_home")
        Gradle(destination.toFile(), outputDir.toFile(), File(gradlePath), null).use { gradle ->
            // Remove unnecessary init script contents (b/294392417)
            val initScript = outputDir.resolve("init.script")
            Files.write(initScript, listOf(""))
            repos.forEach { gradle.addRepo(it) }
            gradle.addArgument("-Dcom.android.gradle.version=$agpVersion")
            gradle.addArgument("-Duser.home=${home.toString()}")
            gradle.run(tasks)
        }
    }
}