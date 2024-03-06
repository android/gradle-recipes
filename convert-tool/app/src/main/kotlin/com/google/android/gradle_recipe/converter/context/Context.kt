/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gradle_recipe.converter.context

import com.google.android.gradle_recipe.converter.converters.FullAgpVersion
import com.google.android.gradle_recipe.converter.converters.ShortAgpVersion
import java.nio.file.Path

/**
 * The context to be used to run and test the recipe conversion
 */
interface Context {

    data class VersionInfo(
        val gradle: String,
        val kotlin: String
    )

    /** Represents the content of the version_mapping file */
    val versionMappings: Map<ShortAgpVersion, VersionInfo>

    val gradleResourceFolder: Path

    fun getPublishedAgp(agp: ShortAgpVersion): FullAgpVersion

    val maxPublishedAgp: FullAgpVersion

    fun getGradleVersion(agp: ShortAgpVersion): String?
    fun getGradleVersion(agp: FullAgpVersion): String? = getGradleVersion(agp.toShort())

    fun getKotlinVersion(agp: ShortAgpVersion): String?
    fun getKotlinVersion(agp: FullAgpVersion): String? = getKotlinVersion(agp.toShort())

    /** Whether validation is being done on CI */
    val ci: Boolean

    /** Location of the repo to replace $AGP_REPOSITORY in the recipe(s), if specified. */
    val repoLocation: String?

    /** Value used for distributionUrl in gradle-wrapper.properties, if specified. */
    val gradlePath: String?

    /** Java home used for validation, if specified. */
    val javaHome: String?

    /** ANDROID_HOME used for validation, if specified. */
    val androidHome: String?
}