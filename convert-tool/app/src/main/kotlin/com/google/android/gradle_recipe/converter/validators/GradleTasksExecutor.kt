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

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import java.lang.System.err
import java.nio.file.Path

/** Executes Gradle tasks via GradleConnector API
 */
class GradleTasksExecutor(projectDir: Path) {
    private val connector: GradleConnector = GradleConnector.newConnector()

    init {
        connector.forProjectDirectory(projectDir.toFile())
    }

    fun executeTasks(tasks: List<String>) {
        tasks.forEach { task ->
            println("$task ")
            executeTask(task)
        }
    }

    private fun executeTask(vararg tasks: String?) {
        try {
            val connection: ProjectConnection = connector.connect()
            val build: org.gradle.tooling.BuildLauncher = connection.newBuild()
            build.forTasks(*tasks)
            build.run()
            connection.close()
        } catch (e: RuntimeException) {
            err.println("Task: ${tasks[0]} failed with, ${e.message}")
        }
    }
}
