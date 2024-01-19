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

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LatestVersionFinderTest {

    private lateinit var mavenMetadataFile: File

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    /**
     * Create a maven metadata file with 4 different major/minor combinations:
     *   8.1 - latest version is 8.1.1
     *   8.2 - latest version is 8.2.0-rc02
     *   8.3 - latest version is 8.3.0-beta02
     *   8.4 - latest version is 8.4.0-alpha02
     *
     * Each major/minor combination also has previous alpha/beta/rc versions when possible.
     */
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
                            <version>8.1.0-alpha01</version>
                            <version>8.1.0-beta01</version>
                            <version>8.1.0-rc01</version>
                            <version>8.1.0</version>
                            <version>8.1.1</version>
                            <version>8.2.0-alpha01</version>
                            <version>8.2.0-beta01</version>
                            <version>8.2.0-rc01</version>
                            <version>8.2.0-rc02</version>
                            <version>8.3.0-alpha01</version>
                            <version>8.3.0-beta01</version>
                            <version>8.3.0-beta02</version>
                            <version>8.4.0-alpha01</version>
                            <version>8.4.0-alpha02</version>
                        </versions>
                        <lastUpdated>20240112173158</lastUpdated>
                    </versioning>
                </metadata>
            """.trimIndent()
        )
    }

    @Test
    fun testFindLatestVersion() {
        assertThat(findLatestVersion(mavenMetadataFile, "8.1")).isEqualTo("8.1.1")
        assertThat(findLatestVersion(mavenMetadataFile, "8.2")).isEqualTo("8.2.0-rc02")
        assertThat(findLatestVersion(mavenMetadataFile, "8.3")).isEqualTo("8.3.0-beta02")
        assertThat(findLatestVersion(mavenMetadataFile, "8.4")).isEqualTo("8.4.0-alpha02")
        assertThat(findLatestVersion(mavenMetadataFile, "8.5")).isEqualTo(null)
        try {
            findLatestVersion(mavenMetadataFile, "8.1.0")
            fail("Expected majorMinorVersion of \"8.1.0\" to throw an exception.")
        } catch (_: RuntimeException) { }
    }
}