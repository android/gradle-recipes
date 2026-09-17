/*
 * Copyright 2024 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.DeviceTestBuilder
import com.android.build.api.variant.HasDeviceTests
import com.android.build.api.variant.HasDeviceTestsBuilder
import com.android.build.api.variant.HasHostTests
import com.android.build.api.variant.HasHostTestsBuilder
import com.android.build.api.variant.HostTestBuilder
import com.android.build.gradle.BasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

/**
 * This custom plugin will register a callback that is applied to all variants for all Android plugins.
 */
class CustomPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        // Registers a callback on the Android base plugin.
        // This allows the CustomPlugin to work whether it's applied before or after the base plugin.
        project.plugins.withType(BasePlugin::class.java) {

            val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

            // Disable tests
            // We will disable the Android tests for the debug variant, and disable unit tests for release
            androidComponents.beforeVariants(androidComponents.selector().withBuildType("debug")) { variantBuilder ->
                // Query the device tests by name to find the Android tests, and disable them
                // This is not available on all sub types of Variant- only those implementing HasDeviceTestsBuilder
                // supports device tests
                (variantBuilder as? HasDeviceTestsBuilder)?.deviceTests?.get(
                    DeviceTestBuilder.ANDROID_TEST_TYPE
                )?.enable = false
            }
            androidComponents.beforeVariants(androidComponents.selector().withBuildType("release")) { variantBuilder ->
                // There are multiple host test types, and they can all be disabled, or they can be queried and
                // disabled individually by name like below for unit tests
                // This is not available on all sub types of Variant- only those implementing HasHostTestsBuilder
                // supports host tests
                (variantBuilder as? HasHostTestsBuilder)?.hostTests?.get(HostTestBuilder.UNIT_TEST_TYPE)?.enable = false
            }

            // -- Verification --
            // the following is just to validate the recipe and is not actually part of the recipe itself
            androidComponents.onVariants { variant ->
                val taskName = "check${variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}TestStatus"
                project.tasks.register<CheckTestStatusTask>(taskName) {
                    // If the unit tests are disabled, the unit test type will not be present in the host tests
                    unitTestEnabled.set(
                        (variant as? HasHostTests)?.hostTests?.get(HostTestBuilder.UNIT_TEST_TYPE) != null
                    )
                    // If the Android tests are disabled, the Android test type will not be present in the device tests
                    androidTestEnabled.set(
                        (variant as? HasDeviceTests)?.deviceTests?.get(DeviceTestBuilder.ANDROID_TEST_TYPE) != null
                    )
                    variantName.set(variant.name)
                    output.set(project.layout.buildDirectory.dir("intermediates/$taskName"))
                }
            }
        }
    }
}