/*
 * Copyright 2024 Google, Inc.
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

package com.google.android.gradle_recipe.converter

import com.github.rising3.semver.SemVer
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.system.exitProcess

/**
 * Entry point to call [findLatestVersion]
 */
fun main(args: Array<String>) {
    if (args.size != 2 || args.contains("--help")) {
        System.err.println("Usage: LatestVersionFinder <mavenMetadataFile> <majorMinorVersion>")
        exitProcess(1)
    }
    val mavenMetadataFile = args[0]
    val majorMinorVersion = args[1]
    val latestVersion =
        findLatestVersion(File(mavenMetadataFile), majorMinorVersion)
            ?: throw RuntimeException("Unable to find any version starting with $majorMinorVersion")
    println(latestVersion)
}

/**
 * Parse [mavenMetadataFile] and return the latest version that begins with
 * [majorMinorVersion], or return null if no such version found.
 *
 * [mavenMetadataFile] is assumed to be a xml file in the expected maven metadata format.
 */
fun findLatestVersion(mavenMetadataFile: File, majorMinorVersion: String): String? {
    validateMajorMinorVersion(majorMinorVersion)
    val versionNodeList =
        DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(mavenMetadataFile)
            .getElementsByTagName("version")
    var maxVersion: String? = null
    for (i in 0 until versionNodeList.length) {
        val version = versionNodeList.item(i).textContent
        if (version.startsWith("$majorMinorVersion.")) {
            maxVersion = when {
                maxVersion == null -> version
                SemVer.parse(version) > SemVer.parse(maxVersion) -> version
                else -> maxVersion
            }
        }
    }
    return maxVersion
}

/**
 * Throw a RuntimeException if majorMinorVersion not formatted as "X.Y", where X and Y are
 * both positive whole numbers
 */
private fun validateMajorMinorVersion(majorMinorVersion: String) {
    val pattern = Regex("^\\d+\\.\\d+$")
    if (pattern.matches(majorMinorVersion)) {
        return
    }
    throw RuntimeException(
        "majorMinorVersion must be formatted as \"X.Y\", where X and Y are positive " +
                "whole numbers."
    )
}