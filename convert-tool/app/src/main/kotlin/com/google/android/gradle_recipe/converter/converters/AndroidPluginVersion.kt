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

import com.google.android.gradle_recipe.converter.converters.AndroidPluginVersion.PreviewType
import java.util.Locale
import java.util.regex.Pattern

/**
 * Represents a version of the Android Gradle Plugin
 */
data class AndroidPluginVersion(
    /**
     * The major version.
     *
     * e.g. 7 for Android Gradle Plugin Version 7.0.1
     */
    val major: Int,
    /**
     * The minor version.
     *
     * e.g. 0 for Android Gradle Plugin Version 7.0.1
     */
    val minor: Int,
    /**
     * The micro, or patch version.
     *
     * e.g. 1 for Android Gradle Plugin Version 7.0.1
     */
    val micro: Int,
    val previewType: PreviewType = PreviewType.FINAL,

    /**
     * The preview version.
     *
     * e.g. 5 for Android Gradle Plugin Version 7.0.0-alpha05. 0 if [PreviewType.FINAL] or [PreviewType.DEV]
     */
    val preview: Int = 0,
) : Comparable<AndroidPluginVersion> {

    enum class PreviewType(val publicName: String?) {
        ALPHA("alpha"),
        BETA("beta"),
        RC("rc"),
        DEV("dev"), // dev is always the latest in the branch, and therefore is higher than all published previews
        FINAL(null);

        companion object {
            fun from(value: String): PreviewType {
                return valueOf(value.uppercase(Locale.getDefault()))
            }
        }
    }

    override fun compareTo(other: AndroidPluginVersion): Int {
        return comparator.compare(this, other)
    }

    override fun toString(): String {
        return version
    }

    /**
     * Returns the string representing this AGP version in maven version form.
     *
     * This corresponds exactly to the format of the version of the Android Gradle plugin artifacts
     * published in the Google maven repository.
     */
    val version: String = buildString {
        append(major).append('.').append(minor).append('.').append(micro)
        // This duplicates encoding the same special cases as in AgpVersion. Sadly it's challenging
        // to share code as gradle-api should  have as few dependencies as possible.
        // See AndroidPluginVersionTest
        when (previewType) {
            PreviewType.FINAL -> {}
            PreviewType.DEV -> append("-dev")
            else -> {
                append('-').append(previewType.publicName)
                val isTwoDigitPreviewFormat = major > 3 ||
                        major == 3 && minor > 1 ||
                        major == 3 && minor == 1 && micro == 0 && previewType != PreviewType.BETA
                if (isTwoDigitPreviewFormat && preview < 10) append('0')
                append(preview)
            }
        }
    }

    companion object {
        private val comparator: Comparator<AndroidPluginVersion> =
            Comparator.comparingInt<AndroidPluginVersion> { it.major }
                .thenComparingInt { it.minor }
                .thenComparingInt { it.micro }
                .thenComparingInt { it.previewType.ordinal }
                .thenComparingInt { it.preview }

        private val VERSION_REGEX: Pattern = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)(-.+)?$")
        private val PREVIEW_REGEX: Pattern = Pattern.compile("^([a-z]+)(\\d+)$")

        fun parse(versionString: String): AndroidPluginVersion {
            val matcher = VERSION_REGEX.matcher(versionString)
            if (matcher.matches()) {
                val previewTag= matcher.group(4)?.substring(1)
                val (previewType, preview) = previewTag?.let {
                    if (previewTag == "dev") {
                        (PreviewType.DEV to 0)
                    } else {
                        val matcher2 = PREVIEW_REGEX.matcher(it)
                        if (matcher2.matches()) {
                            PreviewType.from(matcher2.group(1)) to matcher2.group(2).toInt()
                        } else throw RuntimeException("Unable to parse AGP version '$versionString'. Preview type is malformed")
                    }
                } ?: (PreviewType.FINAL to 0)

                return AndroidPluginVersion(
                    matcher.group(1).toInt(),
                    matcher.group(2).toInt(),
                    matcher.group(3)?.toInt() ?: 0,
                    previewType,
                    preview
                )
            }
            throw RuntimeException("Unable to parse AGP version '$versionString'")
        }
    }
}
