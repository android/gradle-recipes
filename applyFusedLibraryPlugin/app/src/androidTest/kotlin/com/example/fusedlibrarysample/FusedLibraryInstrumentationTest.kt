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

package com.example.fusedlibrarysample

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson // from :fusedLibrary

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumentation tests demonstrating example use cases of the fused library plugin.
 */
@RunWith(AndroidJUnit4::class)
class FusedLibraryInstrumentationTest {

    @Test
    fun accessResourcesFromFusedLibrary() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val stringOverridden =
            appContext.getString(com.example.fusedlibrary.R.string.string_overridden)
        val appStringReferencingFusedLibraryString =
            appContext.getString(R.string.fused_lib_reference)
        assertEquals(stringOverridden, "androidLib2")
        assertEquals(appStringReferencingFusedLibraryString, "androidLib1")
    }

    @Test
    fun accessExternalLibrariesFromIncludeDependencies() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val androidLib1AssetInputStream = appContext.assets.open("androidLib1-asset.json")
        val jsonString = androidLib1AssetInputStream.bufferedReader().use {it.readText() }
        val json = Gson().fromJson(jsonString, JsonAsset::class.java)
        assertEquals(json.info, "This is an asset from androidLib1")
    }

    private class JsonAsset(val info: String)
}