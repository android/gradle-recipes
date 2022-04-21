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

/** The functions in this file perform the conversion by  adding and removing
 *  the placeholder data. The functions swap the placeholders
 * ($AGP_VERSION, $AGP_REPOSITORY and $GRADLE_LOCATION) with actual
 *  values and commenting the relevant lines to use later
 */

/**
 *  for build.gradle[.kts] , settings.gradle[.kts] ==> wraps $AGP_VERSION or $AGP_REPOSITORY
 */
fun wrapGradlePlaceholders(originalLines: List<String>, from: String, to: String): List<String> {
    return wrapPlaceholders(originalLines, from, to, "//  ")
}

/**
 *  for build.gradle[.kts] , settings.gradle[.kts] ==> unwraps $AGP_VERSION or $AGP_REPOSITORY
 */
fun unwrapGradlePlaceholders(originalLines: List<String>): List<String> {
    return unwrapPlaceholders(originalLines, "//  ")
}

/**
 *  for gradle.wrapper.properties ==> wraps $GRADLE_LOCATION
 */
fun wrapGradleWrapperPlaceholders(originalLines: List<String>, from: String, to: String): List<String> {
    return wrapPlaceholders(originalLines, from, to, "#  ")
}

/**
 *  for gradle.wrapper.properties ==> unwraps $GRADLE_LOCATION
 */
fun unwrapGradleWrapperPlaceholders(originalLines: List<String>): List<String> {
    return unwrapPlaceholders(originalLines, "#  ")
}

/**
 *  replaces placeholders with actual values, without keeping the placeholders
 */
fun replacePlaceHolderWithValue(originalLines: List<String>, placeHolder: String, value: String): List<String> {
    val result = buildList {
        for (line in originalLines) {
            if (line.contains(placeHolder)) {
                if (value.isNotEmpty()) {
                    add(line.replace(placeHolder, value))
                }

            } else {
                add(line)
            }
        }
    }

    return result
}

/**
 *  wraps both gradle and properties with commentOut
 */
private fun wrapPlaceholders(originalLines: List<String>, from: String, to: String, commentOut: String): List<String> {
    val result = buildList {
        for (line in originalLines) {
            if (line.contains(from)) {
                add("$commentOut>>> WORKING_COPY >>>")
                add("$commentOut$line")

                if (to.isNotEmpty()) {
                    val newLine: String = line.replace(from, to)
                    add(newLine)
                }

                add("$commentOut<<< WORKING_COPY <<<")
            } else {
                add(line)
            }
        }
    }

    return result
}

/**
 * unwraps both gradle and properties with commentOut
 */
private fun unwrapPlaceholders(originalLines: List<String>, commentOut: String): List<String> {
    var insideWorkBlock = false
    val result = buildList {
        for (line in originalLines) {
            if (line == "$commentOut>>> WORKING_COPY >>>") {
                insideWorkBlock = true
            } else if (insideWorkBlock) {
                if (line != "$commentOut<<< WORKING_COPY <<<") {
                    if (line.startsWith("$commentOut")) {
                        add(line.substring(commentOut.length))
                    }

                } else {
                    // we are in "<<< WORKING_COPY <<<", means finished the block
                    insideWorkBlock = false
                }
            } else {
                add(line)
            }
        }
    }

    return result
}
