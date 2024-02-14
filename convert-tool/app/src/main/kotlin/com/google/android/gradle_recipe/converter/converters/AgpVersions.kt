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

package com.google.android.gradle_recipe.converter.converters

import com.github.rising3.semver.SemVer
import com.google.android.gradle_recipe.converter.converters.ShortAgpVersion.Companion.isShortVersion
import com.google.android.gradle_recipe.converter.printErrorAndTerminate
import com.google.android.gradle_recipe.converter.recipe.toMajorMinor

// in order to make it clearer what versions of AGP the code expect, we are creating String wrapper to
// use type-safety

interface AgpVersion
/**
 * A full version for AGP, as published in the maven repository.
 *
 * Example: 8.3.0, 8.4.0-alpha01
 */
data class FullAgpVersion(val value: String): AgpVersion, Comparable<FullAgpVersion> {
    companion object {
        /**
         * Wraps the given string with a [FullAgpVersion]. The value must be a properly
         * formatted full version that is published
         */
        fun of(value: String) = FullAgpVersion(value)

        /**
         * From the given version, attempts to find a matching published AGP version
         */
        fun String.toPublishedAgp(): FullAgpVersion = if (isShortVersion()) {
            val version = ShortAgpVersion.of(this)
            getVersionsFromAgp(version)?.agp ?: printErrorAndTerminate("AGP version '$this' does not match a published AGP version")
        } else {
            of(this)
        }
    }

    fun toShort() = ShortAgpVersion.of(value.toMajorMinor())
    override fun compareTo(other: FullAgpVersion): Int {
        return SemVer.parse(value).compareTo(SemVer.parse(other.value))
    }

    override fun toString(): String = value
}

/**
 * A short AGP version with only major and minor version
 *
 * Example: 8.3, 8.4
 */
data class ShortAgpVersion(val value: String): AgpVersion, Comparable<ShortAgpVersion> {
    companion object {
        fun of(value: String) = ShortAgpVersion(value)

        fun String.isShortVersion(): Boolean {
            return this.matches(Regex("^\\d+\\.\\d+$"))
        }

        fun String.convertIfMatch(): ShortAgpVersion? {
            return if (this.isShortVersion()) {
                of(this)
            } else null
        }
    }

    override fun compareTo(other: ShortAgpVersion): Int {
        // in order to be able to parse with SemVer, we need x.y.z but this class only deals with
        // x.y, so we add a .0
        return SemVer.parse(value + ".0").compareTo(SemVer.parse(other.value + ".0"))
    }

    override fun toString(): String = value
}
