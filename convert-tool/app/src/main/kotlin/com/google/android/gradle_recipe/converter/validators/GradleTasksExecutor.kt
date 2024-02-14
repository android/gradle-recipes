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

package com.google.android.gradle_recipe.converter.validators

import org.gradle.tooling.BuildException
import org.gradle.tooling.GradleConnector
import java.nio.file.Path

/** Executes Gradle tasks via GradleConnector API
 */
class GradleTasksExecutor(projectDir: Path) {
    private val connector: GradleConnector = GradleConnector.newConnector()

    init {
        connector.forProjectDirectory(projectDir.toFile())
    }

    fun executeTasks(tasks: List<String>) {
        try {
            println("Executing tasks: $tasks")
            connector.connect().use { connection ->
                val build: org.gradle.tooling.BuildLauncher = connection.newBuild()
                build.forTasks(*tasks.toTypedArray()).run()
            }
        } catch (e: BuildException) {
            throw e
        }
    }
}
