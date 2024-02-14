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

import java.util.regex.Pattern

// in order to make it clearer what versions of AGP the code expect, we are creating String wrapper to
// use type-safety

interface AgpVersion
/**
 * A full version for AGP, as published in the maven repository.
 *
 * Example: 8.3.0, 8.4.0-alpha01
 */
data class FullAgpVersion(val version: AndroidPluginVersion): AgpVersion, Comparable<FullAgpVersion> {

    companion object {
        /**
         * Wraps the given string with a [FullAgpVersion]. The value must be a properly
         * formatted full version that is published
         */
        fun of(value: String) = FullAgpVersion(AndroidPluginVersion.parse(value))
    }

    fun toShort(): ShortAgpVersion = ShortAgpVersion(version.major, version.minor)

    override fun compareTo(other: FullAgpVersion): Int {
        return this.version.compareTo(other.version)
    }

    override fun toString(): String = version.version
}

/**
 * A short AGP version with only major and minor version
 *
 * Example: 8.3, 8.4
 */
data class ShortAgpVersion(val major: Int, val minor: Int): AgpVersion, Comparable<ShortAgpVersion> {
    companion object {
        private val VERSION_REGEX: Pattern = Pattern.compile("^(\\d+)\\.(\\d+)$")

        fun ofOrNull(value: String): ShortAgpVersion? {
            val matcher = VERSION_REGEX.matcher(value)
            if (matcher.matches()) {
                return ShortAgpVersion(matcher.group(1).toInt(), matcher.group(2).toInt())
            }

            return null
        }

        private val comparator: Comparator<ShortAgpVersion> =
            Comparator.comparingInt<ShortAgpVersion> { it.major }
                .thenComparingInt { it.minor }
    }

    override fun compareTo(other: ShortAgpVersion): Int {
        return comparator.compare(this, other)
    }

    override fun toString(): String = "$major.$minor"
}
