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

import com.google.android.gradle_recipe.converter.printErrorAndTerminate
import java.nio.file.Path

/**
 * The actual conversion logic
 */

private const val START_WORKING_COPY_BLOCK = ">>> WORKING_COPY >>>"
private const val END_WORKING_COPY_BLOCK = "<<< WORKING_COPY <<<"


fun Path.mkdirs(): Boolean {
    val file = toFile()
    val ret = file.mkdirs()
    if (!ret) {
        if (!file.isDirectory) {
            printErrorAndTerminate("Unable to create folder: $this")
        }
    }
    return ret
}

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
fun List<String>.wrapGradlePlaceholdersWithInlineValue(from: String, to: String): List<String> {
    return wrapPlaceholdersWithInlineValue(from, to, "//  ")
}

/**
 *  for build.gradle[.kts] , settings.gradle[.kts] ==> wraps the
 *  $DEPENDENCY_REPOSITORIES
 *  $PLUGIN_REPOSITORIES
 *
 *  with lists of relevant repositories
 */
fun List<String>.wrapGradlePlaceholdersWithList(from: String, to: List<String>): List<String> {
    return wrapPlaceholdersWithList(from, to, "//  ")
}

/**
 *  for build.gradle[.kts] , settings.gradle[.kts] ==> removes all working copy blocks
 *  and generated lines
 */
fun List<String>.unwrapGradlePlaceholders(): List<String> {
    return unwrapPlaceholders("//  ")
}

/**
 *  for gradle.wrapper.properties ==> wraps $GRADLE_LOCATION
 */
fun List<String>.wrapGradleWrapperPlaceholders(from: String, to: String): List<String> {
    return wrapPlaceholdersWithInlineValue(from, to, "#  ")
}

/**
 *  for libs.versions.toml ==> unwraps all converter placeholders
 */
fun List<String>.unwrapVersionCatalogPlaceholders(): List<String> {
    return unwrapPlaceholders( "#  ")
}

/**
 *  for libs.versions.toml ==> wraps all converter placeholders
 */
fun List<String>.wrapVersionCatalogPlaceholders(from: String, to: String): List<String> {
    return wrapPlaceholdersWithInlineValue(from, to, "#  ")
}

/**
 *  for libs.versions.toml ==> replace all converter placeholders
 */
fun List<String>.replaceVersionCatalogPlaceholders(from: String, to: String): List<String> {
    return replaceGradlePlaceholdersWithInlineValue(from, to)
}

/**
 *  replaces placeholders inside line
 */
fun List<String>.replaceGradlePlaceholdersWithInlineValue(
    from: String,
    to: String,
): List<String> {
    val result = buildList {
        for (line: String in this@replaceGradlePlaceholdersWithInlineValue) {
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
fun List<String>.replacePlaceHolderWithLine(placeHolder: String, value: String): List<String> {
    return replacePlaceHolderWithList(placeHolder, if (value.isEmpty()) listOf() else listOf(value))
}

fun List<String>.replacePlaceHolderWithList(
    placeHolder: String,
    values: List<String>,
): List<String> {
    val result = buildList {
        for (line in this@replacePlaceHolderWithList) {
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
private fun List<String>.wrapPlaceholdersWithInlineValue(
    from: String,
    to: String,
    commentOut: String,
): List<String> {
    val result = buildList {
        for (line in this@wrapPlaceholdersWithInlineValue) {
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
private fun List<String>.wrapPlaceholdersWithList(
    from: String,
    to: List<String>,
    commentOut: String,
): List<String> {
    val result = buildList {
        for (line in this@wrapPlaceholdersWithList) {
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
private fun List<String>.unwrapPlaceholders(commentOut: String): List<String> {
    var insideWorkBlock = false
    val result = buildList {
        for (line in this@unwrapPlaceholders) {
            if (line == "$commentOut$START_WORKING_COPY_BLOCK") {
                insideWorkBlock = true
            } else if (insideWorkBlock) {
                if (line != "$commentOut$END_WORKING_COPY_BLOCK") {
                    if (line.startsWith(commentOut)) {
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