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
import com.google.android.gradle_recipe.converter.recipe.toMajorMinor
import java.nio.file.Path
import kotlin.io.path.isDirectory

/** The position of the gradle-resources folder
 *  to take the Gradle wrapper
 */
const val GRADLE_RESOURCES_FOLDER = "gradle-resources"

/** Interface for different converters.
 *  The objects are created and called from the RecipeConverter class,
 *  using a Template Method pattern.
 */
abstract class Converter(
    protected val branchRoot: Path
) {

    var recipe: Recipe? = null


    /** Can converter convert this recipe
     */
    abstract fun isConversionCompliant(recipe: Recipe): Boolean

    /**
     * Converts build.gradle
     */
    abstract fun convertBuildGradle(source: Path, target: Path)

    /**
     * Converts build.gradle.kts ==> same as build.gradle
     */
    fun convertBuildGradleKts(source: Path, target: Path) {
        convertBuildGradle(source, target)
    }

    /**
     * Converts settings.gradle
     */
    abstract fun convertSettingsGradle(source: Path, target: Path)

    /**
     * Converts settings.gradle.kts ==> same as settings.gradle
     */
    fun convertSettingsGradleKts(source: Path, target: Path) {
        convertSettingsGradle(source, target)
    }

    /**
     *  Converts the version catalog file
     */
    abstract fun convertVersionCatalog(source: Path, target: Path)

    /**
     * Copies the gradle folder from the GRADLE_RESOURCES_FOLDER
     * to dest.
     */
    fun copyGradleFolder(dest: Path) {
        val source = branchRoot.resolve(GRADLE_RESOURCES_FOLDER)
        if (!source.isDirectory()) {
            throw RuntimeException("Unable to find gradle resources at $source")
        }

        dest.mkdirs()

        source.toFile().copyRecursively(target = dest.toFile())

        processGradleWrapperProperties(
            dest.resolve("gradle").resolve("wrapper").resolve("gradle-wrapper.properties")
        )

    }

    open fun processGradleWrapperProperties(file: Path) { }

    protected fun getMinAgp(): String = recipe?.minAgpVersion
        ?: error("min Agp version is badly specified in the metadata")

    protected fun getVersionInfoFromAgp(agpVersion: String): VersionInfo {
        val agp = agpVersion.toMajorMinor()
        return getVersionsFromAgp(branchRoot, agp)
            ?: throw RuntimeException("Unable to fetch VersionInfo for AGP $agp")
    }
}