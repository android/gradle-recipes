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

import com.google.android.gradle_recipe.converter.branchRoot
import com.google.android.gradle_recipe.converter.printErrorAndTerminate
import com.google.android.gradle_recipe.converter.recipe.RecipeData
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.isDirectory

/** The position of the gradle-resources folder
 *  to take the Gradle wrapper
 */
const val GRADLE_RESOURCES_FOLDER = "gradle-resources"

/** Interface for different converters.
 *  The objects are created and called from the RecipeConverter class,
 *  using a Template Method pattern.
 */
abstract class Converter {
    protected val DEFAULT_SKIP_FILENAMES = setOf("gradlew", "gradlew.bat", "local.properties")
    protected val DEFAULT_SKIP_FOLDERNAMES = setOf("build", ".idea", ".gradle", "out", "wrapper")

    // some converters may need the minimum AGP version supported by the recipe.
    var minAgp: FullAgpVersion? = null

    protected open val skippedFilenames: Set<String>
        get() = DEFAULT_SKIP_FILENAMES

    protected open val skippedFoldernames: Set<String>
        get() = DEFAULT_SKIP_FOLDERNAMES

    /**
     * A filter for files and folders during a conversion. Filters out Gradle
     * and Android Studio temporary and local files.
     */
    fun accept(file: File): Boolean {
        if (file.isFile) {
            return !skippedFilenames.contains(file.name)
        }

        if (file.isDirectory) {
            return !skippedFoldernames.contains(file.name)
        }

        return true
    }


    /**
     * Can converter convert this recipe
     */
    open fun isConversionCompliant(recipeData: RecipeData): Boolean {
        return true
    }

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
            printErrorAndTerminate("Unable to find gradle resources at $source")
        }

        dest.mkdirs()

        source.toFile().copyRecursively(target = dest.toFile())

        processGradleWrapperProperties(
            dest.resolve("gradle").resolve("wrapper").resolve("gradle-wrapper.properties")
        )

        // we need to reset the file permissions on `gradlew` to make it executable
        val gradlew = dest.resolve("gradlew")
        val currentPermission = Files.getPosixFilePermissions(gradlew)
        Files.setPosixFilePermissions(
            gradlew,
            currentPermission + PosixFilePermission.OWNER_EXECUTE,
        )
    }

    open fun processGradleWrapperProperties(file: Path) { }

    protected fun getVersionInfoFromAgp(agpVersion: FullAgpVersion): VersionInfo {
        return getVersionsFromAgp(agpVersion.toShort())
            ?: printErrorAndTerminate("Unable to fetch VersionInfo for AGP $agpVersion")
    }
}