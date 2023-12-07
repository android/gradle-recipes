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

@file:OptIn(ExperimentalCli::class)

package com.google.android.gradle_recipe.converter

import com.google.android.gradle_recipe.converter.converters.RecipeConverter
import com.google.android.gradle_recipe.converter.converters.RecursiveConverter
import com.google.android.gradle_recipe.converter.converters.convertStringToMode
import com.google.android.gradle_recipe.converter.validators.GithubPresubmitValidator
import com.google.android.gradle_recipe.converter.validators.InternalCIValidator
import com.google.android.gradle_recipe.converter.validators.WorkingCopyValidator
import java.nio.file.Path
import kotlin.system.exitProcess
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default


/**
 * The main entry to the Converter, parser the command line arguments, and calls
 * the relevant classes to perform the work
 */
fun main(args: Array<String>) {
    val toolName = "convert-tool"

    val parser = ArgParser(programName = toolName, useDefaultHelpShortName = true)

    val overwrite by parser.option(ArgType.Boolean, shortName = "o", description = "Overwrite").default(false)
    val source by parser.option(ArgType.String, shortName = "s", description = "Recipe source")
    val sourceAll by parser.option(ArgType.String, shortName = "sa", description = "All recipe sources")
    val destination by parser.option(ArgType.String, shortName = "d", description = "Destination folder")
    val tmpFolder by parser.option(ArgType.String, shortName = "tf", description = "Temp folder")
    val agpVersion by parser.option(ArgType.String, shortName = "a", description = "AGP version")
    val repoLocation by parser.option(ArgType.String, shortName = "rl", description = "Repo location")
    val gradleVersion by parser.option(ArgType.String, shortName = "gv", description = "Gradle version")
    val gradlePath by parser.option(ArgType.String, shortName = "gp", description = "Gradle path")
    val mode by parser.option(ArgType.String, shortName = "m", description = "Mode")

    parser.subcommands(
        object : Subcommand("convert", "Convert a recipe from one state to the other") {
            override fun execute() {
                if (source != null) {
                    RecipeConverter(
                        agpVersion = agpVersion,
                        repoLocation = repoLocation,
                        gradleVersion = gradleVersion,
                        gradlePath = gradlePath,
                        mode = convertStringToMode(mode),
                        overwrite = overwrite
                    ).convert(
                        source = Path.of(source ?: printErrorAndTerminate("source must be specified")),
                        destination = Path.of(destination ?: printErrorAndTerminate("destination must be specified"))
                    )
                } else {
                    RecursiveConverter(
                        agpVersion = agpVersion,
                        repoLocation = repoLocation,
                        gradleVersion = gradleVersion,
                        gradlePath = gradlePath,
                        overwrite = overwrite
                    ).convertAllRecipes(
                        sourceAll = Path.of(sourceAll ?: printErrorAndTerminate("sourceAll must be specified")),
                        destination = Path.of(destination ?: printErrorAndTerminate("destination must be specified"))
                    )
                }
            }
        },
        object : Subcommand("validate", "Validate a recipe") {
            override fun execute() {
                if (agpVersion == null) {
                    if (mode != null) {
                        val cliMode = convertStringToMode(mode)
                        if (cliMode != RecipeConverter.Mode.WORKINGCOPY) {
                            error("The mode value should be either \"workingcopy\" or nothing")
                        }

                        val validator = WorkingCopyValidator()
                        validator.validate(
                            Path.of(source ?: error("Source must not be null"))
                        )
                    } else {
                        val validator = GithubPresubmitValidator()
                        validator.validateAll(
                            Path.of(sourceAll ?: error("SourceAll must not be null"))
                        )
                    }
                } else {
                    val validator = InternalCIValidator(
                        agpVersion = agpVersion ?: error("agpVersion must not be null"),
                        repoLocation = repoLocation ?: error("repoLocation must not be null"),
                        gradlePath = gradlePath ?: error("gradlePath must not be null")
                    )
                    validator.validate(
                        sourceAll = Path.of(sourceAll ?: error("sourceAll must not be null")),
                        tmpFolder = if (tmpFolder != null) Path.of(tmpFolder) else null
                    )
                }
            }
        },
    )

    val result = parser.parse(args)

    if (result.commandName == toolName) {
        println("Missing subcommand. Use $toolName -h to see usage")
        exitProcess(1)
    }
}

fun printErrorAndTerminate(msg: String): Nothing {
    System.err.println(msg)
    exitProcess(1)
}