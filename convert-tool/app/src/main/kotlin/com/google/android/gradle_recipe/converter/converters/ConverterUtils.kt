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

/**
 * The actual conversion logic
 */

private const val START_WORKING_COPY_BLOCK = ">>> WORKING_COPY >>>"
private const val END_WORKING_COPY_BLOCK = "<<< WORKING_COPY <<<"

/** The below functions perform the conversion by adding and removing
 * the placeholder data.
 *
 * 2 actions:
 *  wraps - takes the original line (with placeholders) and wraps it with
 *          WORKING_COPY blocks
 *  replaces - takes the original line and replaces with the actual values
 */

/**
 *  for build.gradle[.kts] , settings.gradle[.kts] ==> wraps the line
 *  id 'com.android.library' version $AGP_VERSION apply false or
 *  id 'com.android.application' version $AGP_VERSION apply false
 *  and replaces the $AGP_VERSION
 */
fun wrapGradlePlaceholdersWithInlineValue(originalLines: List<String>, from: String, to: String): List<String> {
    return wrapPlaceholdersWithInlineValue(originalLines, from, to, "//  ")
}

/**
 *  for build.gradle[.kts] , settings.gradle[.kts] ==> wraps the
 *  $DEPENDENCY_REPOSITORIES
 *  $PLUGIN_REPOSITORIES
 *
 *  with lists of relevant repositories
 */
fun wrapGradlePlaceholdersWithList(originalLines: List<String>, from: String, to: List<String>): List<String> {
    return wrapPlaceholdersWithList(originalLines, from, to, "//  ")
}

/**
 *  for build.gradle[.kts] , settings.gradle[.kts] ==> removes all working copy blocks
 *  and generated lines
 */
fun unwrapGradlePlaceholders(originalLines: List<String>): List<String> {
    return unwrapPlaceholders(originalLines, "//  ")
}

/**
 *  for gradle.wrapper.properties ==> wraps $GRADLE_LOCATION
 */
fun wrapGradleWrapperPlaceholders(originalLines: List<String>, from: String, to: String): List<String> {
    return wrapPlaceholdersWithInlineValue(originalLines, from, to, "#  ")
}

/**
 *  for gradle.wrapper.properties ==> unwraps $GRADLE_LOCATION
 */
fun unwrapGradleWrapperPlaceholders(originalLines: List<String>): List<String> {
    return unwrapPlaceholders(originalLines, "#  ")
}


/**
 *  replaces placeholders inside line
 */
fun replaceGradlePlaceholdersWithInlineValue(
    originalLines: List<String>,
    from: String,
    to: String,
): List<String> {
    val result = buildList {
        for (line in originalLines) {
            if (line.contains(from)) {
                if (to.isNotEmpty()) {
                    val newLine: String = line.replace(from, to)
                    add(newLine)
                }
            } else {
                add(line)
            }
        }
    }

    return result
}

/**
 *  replaces placeholders with a code line
 */
fun replacePlaceHolderWithLine(originalLines: List<String>, placeHolder: String, value: String): List<String> {
    return replacePlaceHolderWithList(originalLines, placeHolder, if (value.isEmpty()) listOf() else listOf(value))
}

fun replacePlaceHolderWithList(
    originalLines: List<String>,
    placeHolder: String,
    values: List<String>,
): List<String> {
    val result = buildList {
        for (line in originalLines) {
            if (line.contains(placeHolder)) {
                for (toLine in values) {
                    add(toLine)
                }
            } else {
                add(line)
            }
        }
    }

    return result
}

/** wraps placeholders, and replaces the placeholder inline
 *
 */
private fun wrapPlaceholdersWithInlineValue(
    originalLines: List<String>,
    from: String,
    to: String,
    commentOut: String,
): List<String> {
    val result = buildList {
        for (line in originalLines) {
            if (line.contains(from)) {
                add("$commentOut$START_WORKING_COPY_BLOCK")
                add("$commentOut$line")

                if (to.isNotEmpty()) {
                    val newLine: String = line.replace(from, to)
                    add(newLine)
                }

                add("$commentOut$END_WORKING_COPY_BLOCK")
            } else {
                add(line)
            }
        }
    }

    return result
}

/** wraps placeholders, and replaces the placeholder with
 *  a list
 */
private fun wrapPlaceholdersWithList(
    originalLines: List<String>,
    from: String,
    to: List<String>,
    commentOut: String,
): List<String> {
    val result = buildList {
        for (line in originalLines) {
            if (line.contains(from)) {
                add("$commentOut$START_WORKING_COPY_BLOCK")
                add("$commentOut$line")
                for (toLine in to) {
                    add(toLine)
                }
                add("$commentOut$END_WORKING_COPY_BLOCK")
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
            if (line == "$commentOut$START_WORKING_COPY_BLOCK") {
                insideWorkBlock = true
            } else if (insideWorkBlock) {
                if (line != "$commentOut$END_WORKING_COPY_BLOCK") {
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
