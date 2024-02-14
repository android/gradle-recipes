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

import com.google.common.truth.Truth
import org.junit.Test

class AndroidPluginVersionTest {
    @Test
    fun testParse() {
        Truth.assertThat("1.2.3".parse()).isEqualTo(AndroidPluginVersion(1,2,3))
        Truth.assertThat("1.2.3-beta01".parse()).isEqualTo(AndroidPluginVersion(1,2,3, AndroidPluginVersion.PreviewType.BETA, 1))
        Truth.assertThat("1.2.3-rc12".parse()).isEqualTo(AndroidPluginVersion(1,2,3, AndroidPluginVersion.PreviewType.RC, 12))
    }

    @Test
    fun finalToFina() {
        Truth.assertThat("7.3.0".parse()).isLessThan("8.4.5".parse())
   }

    @Test
    fun previewToPreview() {
        Truth.assertThat("7.1.0-alpha01".parse()).isLessThan("7.1.0-alpha02".parse())
        Truth.assertThat("7.1.0-beta01".parse()).isLessThan("7.1.0-beta02".parse())
        Truth.assertThat("7.1.0-rc01".parse()).isLessThan("7.1.0-rc02".parse())
    }

    @Test
    fun alphaToBeta() {
        Truth.assertThat("7.1.0-alpha05".parse()).isLessThan("7.1.0-beta01".parse())
        Truth.assertThat("7.1.0-beta01".parse()).isLessThan("7.2.0-alpha01".parse())
    }

    @Test
    fun betaToRc(): Unit {
        Truth.assertThat("7.1.0-beta01".parse()).isLessThan("7.1.0-rc01".parse())
        Truth.assertThat("7.1.0-rc01".parse()).isLessThan("7.2.0-beta01".parse())
    }

    @Test
    fun rcToFinal() {
        Truth.assertThat("7.1.0-rc01".parse()).isLessThan("7.1.0".parse())
        Truth.assertThat("7.1.0".parse()).isLessThan("7.2.0-rc01".parse())
    }

    @Test
    fun devToPreviews() {
        Truth.assertThat("7.1.0-dev".parse()).isGreaterThan("7.1.0-alpha01".parse())
        Truth.assertThat("7.1.0-dev".parse()).isGreaterThan("7.1.0-beta01".parse())
        Truth.assertThat("7.1.0-dev".parse()).isGreaterThan("7.1.0-rc01".parse())

        Truth.assertThat("7.1.0-dev".parse()).isLessThan("7.1.0".parse())
    }

    private fun String.parse(): AndroidPluginVersion? {
        return AndroidPluginVersion.parse(this)
    }
}