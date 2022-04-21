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

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class ConverterUtilsKtTest {

    // build.gradle
    private val buildGradleSource = listOf(
        "id 'com.android.application' version \$AGP_VERSION apply false",
        "id 'com.android.library' version \$AGP_VERSION apply false",
        "id 'org.jetbrains.kotlin.android' version '1.6.10' apply false"
    )

    private val buildGradleWorkingCopy = listOf(
        "//  >>> WORKING_COPY >>>",
        "//  id 'com.android.application' version \$AGP_VERSION apply false",
        "id 'com.android.application' version \"7.4.4\" apply false",
        "//  <<< WORKING_COPY <<<",
        "//  >>> WORKING_COPY >>>",
        "//  id 'com.android.library' version \$AGP_VERSION apply false",
        "id 'com.android.library' version \"7.4.4\" apply false",
        "//  <<< WORKING_COPY <<<",
        "id 'org.jetbrains.kotlin.android' version '1.6.10' apply false",
    )

    @Test
    fun testBuildGradleRelease() {
        val result = wrapGradlePlaceholders(buildGradleSource, "\$AGP_VERSION", "\"7.4.4\"")
        assertThat(result).isEqualTo(buildGradleWorkingCopy)
    }

    @Test
    fun testBuildGradleSource() {
        val result = unwrapGradlePlaceholders(buildGradleWorkingCopy)
        assertThat(result).isEqualTo(buildGradleSource)
    }

    // Tests for settings.gradle
    private val settingsGradleSource = """
pluginManagement {
    repositories {
${'$'}AGP_REPOSITORY
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
${'$'}AGP_REPOSITORY
        google()
        mavenCentral()
    }
}"""

    private val settingsGradleTest = """
pluginManagement {
    repositories {
//  >>> WORKING_COPY >>>
//  ${'$'}AGP_REPOSITORY
//  <<< WORKING_COPY <<<
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
//  >>> WORKING_COPY >>>
//  ${'$'}AGP_REPOSITORY
//  <<< WORKING_COPY <<<
        google()
        mavenCentral()
    }
}"""

    private val settingsGradleRelease = """
pluginManagement {
    repositories {
//  >>> WORKING_COPY >>>
//  ${'$'}AGP_REPOSITORY
..\..\private-repo\
//  <<< WORKING_COPY <<<
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
//  >>> WORKING_COPY >>>
//  ${'$'}AGP_REPOSITORY
..\..\private-repo\
//  <<< WORKING_COPY <<<
        google()
        mavenCentral()
    }
}"""

    @Test
    fun testSettingsGradleWorkingCopy() {
        val result = wrapGradlePlaceholders(settingsGradleSource.lines(), "\$AGP_REPOSITORY", "")
        assertThat(result).isEqualTo(settingsGradleTest.lines())
    }

    @Test
    fun testSettingsGradleSource() {
        val result = unwrapGradlePlaceholders(settingsGradleTest.lines())
        assertThat(result).isEqualTo(settingsGradleSource.lines())
    }

    @Test
    fun testSettingsGradleRelease() {
        val result = wrapGradlePlaceholders(settingsGradleSource.lines(), "\$AGP_REPOSITORY", "..\\..\\private-repo\\")
        assertThat(result).isEqualTo(settingsGradleRelease.lines())
    }

    // gradle wrapper tests
    private val gradleWrapperSource = listOf(
        "distributionUrl=\$GRADLE_LOCATION"
    )

    private val gradleWrapperRelease = listOf(
        "#  >>> WORKING_COPY >>>",
        "#  distributionUrl=\$GRADLE_LOCATION",
        "distributionUrl=../../../tools/external/gradle/gradle-7.4-bin.zip",
        "#  <<< WORKING_COPY <<<",
    )

    @Test
    fun testGradleWrapperRelease() {
        val result =
            wrapGradleWrapperPlaceholders(
                gradleWrapperSource,
                "\$GRADLE_LOCATION",
                "../../../tools/external/gradle/gradle-7.4-bin.zip"
            )
        assertThat(result).isEqualTo(gradleWrapperRelease)
    }

    @Test
    fun testGradleWrapperSource() {
        val result = unwrapGradleWrapperPlaceholders(gradleWrapperRelease)
        assertThat(gradleWrapperSource).isEqualTo(result)
    }
}