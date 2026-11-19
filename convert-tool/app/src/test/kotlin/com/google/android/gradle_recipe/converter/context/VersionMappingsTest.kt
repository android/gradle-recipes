/*
 * Copyright (C) 2026 The Android Open Source Project
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

import com.google.android.gradle_recipe.converter.converters.ShortAgpVersion
import com.google.common.truth.Truth.assertWithMessage
import java.nio.file.Paths
import org.junit.Assume.assumeTrue
import org.junit.Test

/**
 * Checks that version_mappings.txt lists the AGP major.minor version currently in development
 * (from tools/base/common/release_version.bzl), so recipe tests and GitHub publishing pick it up.
 *
 * Only runs in Bazel, which passes the dev_agp_major_minor property.
 */
class VersionMappingsTest {

    @Test
    fun devAgpVersionIsListed() {
        val devAgpMajorMinor = System.getProperty("dev_agp_major_minor")
        assumeTrue(devAgpMajorMinor != null)

        val context =
            DefaultContext.createDefaultContext(rootFolder = Paths.get("tools/gradle-recipes"))
        val devAgp =
            ShortAgpVersion.ofOrNull(devAgpMajorMinor!!)
                ?: error("Unable to parse dev_agp_major_minor=$devAgpMajorMinor")

        assertWithMessage(
            "AGP $devAgpMajorMinor (BUILD_VERSION in tools/base/common/release_version.bzl) is " +
                "missing from tools/gradle-recipes/version_mappings.txt. Add a " +
                "\"$devAgpMajorMinor;<gradle_version>;<kotlin_version>\" line, e.g. reusing the " +
                "Gradle version from GRADLE_LATEST_VERSION and the Kotlin version of the previous line."
        ).that(context.versionMappings.keys).contains(devAgp)
    }
}
