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

package com.google.android.gradle_recipe.converter

import com.github.rising3.semver.SemVer
import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class AGPVersionsTest {
    @Test
    fun alphaBeta() =
        assertThat(SemVer.parse("7.1.0-alpha") < SemVer.parse("7.1.0-beta5")).isTrue()

    @Test
    fun betaRc() = assertThat(SemVer.parse("7.1.0-beta") < SemVer.parse("7.1.0-rc5")).isTrue()

    @Test
    fun rc1Rc2() =
        assertThat(SemVer.parse("7.1.0-rc1") < SemVer.parse("7.1.0-rc2")).isTrue()

    @Test
    fun rcFinal() = assertThat(SemVer.parse("7.1.0-rc1") < SemVer.parse("7.1.0")).isTrue()

    @Test
    fun finalFinal() = assertThat(SemVer.parse("7.1.0") < SemVer.parse("8.1.0")).isTrue()
}