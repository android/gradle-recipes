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

import com.google.android.gradle_recipe.converter.context.Context
import org.gradle.tooling.BuildException
import org.gradle.tooling.GradleConnector
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

/**
 * Executes Gradle tasks via GradleConnector API
 *
 * If [context].ci is true, temporary directories are used for Gradle and Android user homes, and
 * some arguments and environment variables are set to allow the Gradle invocation to work on CI.
 */
class GradleTasksExecutor(
    projectDir: Path,
    private val context: Context
) {
    private val connector: GradleConnector = GradleConnector.newConnector()
    private val tempDir = createTempDir()

    init {
        connector.forProjectDirectory(projectDir.toFile())
        if (context.ci) {
            connector.useGradleUserHomeDir(
                tempDir.resolve("gradle_user_home")
                    .toFile()
                    .also { it.mkdirs() }
            )
            // Workaround for issue https://github.com/gradle/gradle/issues/5188
            System.setProperty("gradle.user.home", "")
        }
    }

    fun executeTasks(tasks: List<String>) {
        val stderrFile = tempDir.resolve("stderr.txt").toFile()
        val stdoutFile = tempDir.resolve("stdout.txt").toFile()
        try {
            BufferedOutputStream(FileOutputStream(stderrFile)).use { stderr ->
                BufferedOutputStream(FileOutputStream(stdoutFile)).use { stdout ->
                    println("Executing tasks: $tasks")
                    connector.connect().use { connection ->
                        val build: org.gradle.tooling.BuildLauncher = connection.newBuild()
                        context.repoLocation?.let {
                            build.addArguments("-PinjectedMavenRepo=$it")
                        }
                        context.javaHome?.let {
                            build.setJavaHome(File(it))
                            build.addArguments("-Porg.gradle.java.installations.paths=$it")
                        }
                        val environmentVariables = mutableMapOf<String, String>()
                        context.androidHome?.let {
                            environmentVariables["ANDROID_HOME"] = it
                        }
                        if (context.ci) {
                            environmentVariables["ANDROID_USER_HOME"] =
                                tempDir.resolve("android_user_home")
                                    .toFile()
                                    .also { it.mkdirs() }
                                    .absolutePath
                            System.getenv("SystemRoot")?.let {
                                environmentVariables["SystemRoot"] = it
                            }
                            System.getenv("TEMP")?.let {
                                environmentVariables["TEMP"] = it
                            }
                            System.getenv("TMP")?.let {
                                environmentVariables["TMP"] = it
                            }
                            val localMavenRepo =
                                tempDir.resolve("local_maven_repo")
                                    .toFile()
                                    .also { it.mkdirs() }
                                    .absolutePath
                            build.addArguments(
                                "--info",
                                "--stacktrace",
                                "--offline",
                                "-Dmaven.repo.local=${localMavenRepo}",
                            )
                            build.setStandardError(stderr)
                            build.setStandardOutput(stdout)
                        }
                        build.setEnvironmentVariables(environmentVariables)
                        build.forTasks(*tasks.toTypedArray()).run()
                    }
                }
            }
        } catch (e: BuildException) {
            if (context.ci) {
                System.err.println("=================== Stderr ===================")
                System.err.println(stderrFile.readText())
                System.err.println("=================== Stdout ===================")
                System.err.println(stdoutFile.readText())
                System.err.println("==============================================")
                System.err.println("=============== End last build ===============")
                System.err.println("==============================================")
            }
            throw e
        }
    }

    private fun createTempDir(): Path {
        val tempDir = createTempDirectory()
        // Delete temp dir when JVM shuts down
        Runtime.getRuntime().addShutdownHook(
            Thread { tempDir.toFile().deleteRecursively() }
        )
        return tempDir
    }
}
