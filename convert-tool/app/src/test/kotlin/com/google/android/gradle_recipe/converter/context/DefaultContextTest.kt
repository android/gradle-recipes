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

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Path
import kotlin.io.path.writeText

class DefaultContextTest {

    private lateinit var mavenMetadataFile: File
    private lateinit var rootFolder: Path

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Before
    fun before() {
        mavenMetadataFile = temporaryFolder.newFile("maven-metadata.xml")
        mavenMetadataFile.writeText(
            """
                <?xml version='1.0' encoding='UTF-8'?>
                <metadata>
                    <groupId>com.android.tools.build</groupId>
                    <artifactId>gradle</artifactId>
                    <versioning>
                        <latest>8.4.0-alpha02</latest>
                        <release>8.4.0-alpha02</release>
                        <versions>
                            <version>8.1.0</version>
                            <version>8.2.0</version>
                            <version>8.3.0</version>
                            <version>8.4.0-alpha01</version>
                            <version>8.4.0-alpha02</version>
                        </versions>
                        <lastUpdated>20240112173158</lastUpdated>
                    </versioning>
                </metadata>
            """.trimIndent()
        )

        rootFolder = temporaryFolder.newFolder().toPath()
        rootFolder.resolve(VERSION_MAPPING)
            .writeText(
                """
                    # mapping of AGP versions to Gradle and Kotlin Versions, separated by ';'
                    8.1;8.0;1.8.10
                    8.2;8.2;1.8.10
                    8.3;8.4;1.9.20
                    8.4;8.6;1.9.20
                    8.5;8.6;1.9.20
                """.trimIndent()
            )
    }

    /**
     * Test that context.maxPublishedAgp is correct (8.4.0-alpha02) even though a higher AGP
     * version (8.5) is listed in the version_mappings.txt file.
     */
    @Test
    fun testMaxPublishedAgp() {
        val context =
            DefaultContext(
                versionMappingFile = rootFolder.resolve(VERSION_MAPPING),
                gradleResourceFolder = rootFolder.resolve(GRADLE_RESOURCES_FOLDER),
                mavenMetadataFile = mavenMetadataFile
            )
        assertThat(context.maxPublishedAgp.toString()).isEqualTo("8.4.0-alpha02")
    }
}