/*
 * Copyright (C) 2019 The Android Open Source Project
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
import com.android.build.api.extension.ApplicationAndroidComponentsExtension
import com.android.build.api.extension.LibraryAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class CustomPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withType(AppPlugin::class.java) {
            val extension = project.extensions.getByName("androidComponents") as ApplicationAndroidComponentsExtension
            extension.beforeVariants {
                // disable all unit tests for apps (only using instrumentation tests)
                it.enableUnitTest = false
            }
        }
        project.plugins.withType(LibraryPlugin::class.java) {
            val extension = project.extensions.getByName("androidComponents") as LibraryAndroidComponentsExtension
            extension.beforeVariants(extension.selector().withBuildType("debug")) {
                // Disable instrumentation for debug
                it.enableAndroidTest = false
            }
            extension.beforeVariants(extension.selector().withBuildType("release")) {
                // disable all unit tests for apps (only using instrumentation tests)
                it.enableUnitTest = false
            }
        }
    }
}