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

import A_DoIExist
import com.example.androidlib1.ClassFromAndroidLib1 // From :fusedLibrary
import com.example.androidlib2.ClassFromAndroidLib2 // From :fusedLibrary
import org.junit.Test

import org.junit.Assert.*

/**
 * Demonstrates usage of classes that can be accessed from the fused library.
 */
class FusedLibraryConsumptionUnitTest {

    @Test
    fun testClassesFromIncludedDependencies() {
        // Access symbols from local library dependencies
        val androidLib1Class = ClassFromAndroidLib1()
        val androidLib2Class = ClassFromAndroidLib2()

        assertEquals(androidLib1Class.foo(), "foo")
        assertEquals(androidLib2Class.foo(), "bar")

        // Do not uncomment.
        // Classes from androidLib3 are not accessible as it has not been explicitly included in
        // :fusedLibrary. To access classes, add include(project(":androidLib3") to fusedLibrary
        // dependencies
        // val androidLib3Class = ClassFromAndroidLIb3
    }

    @Test
    fun testLocalLibJarDependenciesAreAccessible() {
        // Access class in local jar from fused library file dependencies
        // A_DoIExist is class from a libs jar file dependency
        // (fusedLibrary/libs/simple-jar-with-A_DoIExist-class.jar)
        A_DoIExist()
    }

}