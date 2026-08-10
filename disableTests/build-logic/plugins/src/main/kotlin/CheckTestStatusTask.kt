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

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.lang.RuntimeException

abstract class CheckTestStatusTask : DefaultTask() {

    // In order for the task to be up-to-date when the inputs have not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @get:Input
    abstract val unitTestEnabled: Property<Boolean>

    @get:Input
    abstract val androidTestEnabled: Property<Boolean>

    @get:Input
    abstract val variantName: Property<String>

    @TaskAction
    fun taskAction() {
        if (variantName.get() == "debug" && (!unitTestEnabled.get() || androidTestEnabled.get())) {
            throw RuntimeException("The debug variant should have Android tests disabled.")
        }
        // The Android test is disabled by default for the release variant, so we can validate that
        // both unit tests and Android tests are disabled
        if (variantName.get() == "release" && (androidTestEnabled.get() || unitTestEnabled.get())) {
            throw RuntimeException("The release variant should have unit tests disabled.")
        }
    }
}
