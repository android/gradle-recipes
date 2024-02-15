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

import com.google.android.gradle_recipe.converter.context.Context
import com.google.android.gradle_recipe.converter.converters.FullAgpVersion
import com.google.android.gradle_recipe.converter.converters.RecipeConverter
import com.google.android.gradle_recipe.converter.converters.RecipeConverter.Mode
import com.google.android.gradle_recipe.converter.converters.RecipeConverter.Mode.RELEASE
import com.google.android.gradle_recipe.converter.converters.RecipeConverter.Mode.SOURCE
import com.google.android.gradle_recipe.converter.converters.RecipeConverter.Mode.WORKINGCOPY
import com.google.android.gradle_recipe.converter.converters.RecursiveConverter
import com.google.android.gradle_recipe.converter.validators.GithubPresubmitValidator
import com.google.android.gradle_recipe.converter.validators.InternalCIValidator
import com.google.android.gradle_recipe.converter.validators.WorkingCopyValidator
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.system.exitProcess
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default

const val TOOL_NAME = "convert-tool"
const val COMMAND_VALIDATE = "validate"
const val COMMAND_CONVERT = "convert"
const val COMMAND_VALIDATE_CI = "validateCI"

/**
 * The main entry to the Converter, parser the command line arguments, and calls
 * the relevant classes to perform the work
 */
fun main(args: Array<String>) {
    val parser = ArgParser(programName = TOOL_NAME, useDefaultHelpShortName = true)

    val overwrite by parser.option(ArgType.Boolean, shortName = "o", description = "Overwrite").default(false)
    val source by parser.option(ArgType.String, shortName = "s", description = "Recipe source: The folder directly containing a single recipe.")
    val sourceAll by parser.option(ArgType.String, shortName = "sa", description = "All recipe sources: The folder containing recipe folders")
    val destination by parser.option(ArgType.String, shortName = "d", description = "Destination folder. New folders will be created for each recipe.")
    val tmpFolder by parser.option(ArgType.String, shortName = "tf", description = "Temp folder")
    val agpVersion by parser.option(ArgType.String, shortName = "a", description = "AGP version")
    val repoLocation by parser.option(ArgType.String, shortName = "rl", description = "Repo location")
    val gradleVersion by parser.option(ArgType.String, shortName = "gv", description = "Gradle version")
    val gradlePath by parser.option(ArgType.String, shortName = "gp", description = "Gradle path")
    val mode by parser.option(
        ArgType.Choice<Mode>(),
        shortName = "m",
        description = "Conversion Mode",
    )

    parser.subcommands(
        object : Subcommand(
            COMMAND_CONVERT,
            "Convert one or more recipes from one state to the other (default mode is $RELEASE)"
        ) {
            override fun execute() {
                val finalSource = source
                val finalSourceAll = sourceAll

                val destinationPath: Path =
                    Path.of(destination ?: printErrorAndTerminate("destination must be specified"))

                if (!destinationPath.isDirectory()) {
                    printErrorAndTerminate("Folder does not exist: ${destinationPath.toAbsolutePath()}")
                }

                if (finalSource != null) {
                    RecipeConverter(
                        context = Context.standalone(),
                        agpVersion = agpVersion?.let { FullAgpVersion.of(it) },
                        repoLocation = repoLocation,
                        gradleVersion = gradleVersion,
                        gradlePath = gradlePath,
                        mode = mode ?: RELEASE,
                    ).convert(
                        source = Path.of(finalSource),
                        destination = destinationPath,
                        overwrite = overwrite
                    )
                } else if (finalSourceAll != null) {
                    // if we do a convert all then we expect the root folder to be (mostly) empty
                    // (hidden files, like git files, are kept)
                    if (!destinationPath.isEmptyExceptForHidden()) {
                        if (!overwrite) {
                            printErrorAndTerminate("the destination $destinationPath folder is not empty, call converter with --overwrite to overwrite it")
                        } else {
                            destinationPath.deleteNonHiddenRecursively()
                        }
                    }

                    RecursiveConverter(
                        context = Context.standalone(),
                        agpVersion = agpVersion?.let { FullAgpVersion.of(it) },
                        repoLocation = repoLocation,
                        gradleVersion = gradleVersion,
                        gradlePath = gradlePath,
                    ).convertAllRecipes(
                        sourceAll = Path.of(finalSourceAll),
                        destination = destinationPath
                    )
                } else {
                    printErrorAndTerminate("one of source or sourceAll must be specified")
                }
            }
        },
        object : Subcommand(COMMAND_VALIDATE, "Validate one or more recipes") {
            override fun execute() {
                // ensure no extra/unused values
                validateNullArg(destination, "'destination' must not be provided for subcommand '$COMMAND_VALIDATE'")
                validateNullArg(tmpFolder, "'tmpFolder' must not be provided for subcommand '$COMMAND_VALIDATE'")
                validateNullArg(agpVersion, "'agpVersion' must not be provided for subcommand '$COMMAND_VALIDATE'")
                validateNullArg(repoLocation, "'repoLocation' must not be provided for subcommand '$COMMAND_VALIDATE'")
                validateNullArg(gradleVersion, "'gradleVersion' must not be provided for subcommand '$COMMAND_VALIDATE'")
                validateNullArg(gradlePath, "'gradlePath' must not be provided for subcommand '$COMMAND_VALIDATE'")

                // check the env var for the SDK exist
                if (System.getenv("ANDROID_HOME") == null) {
                    printErrorAndTerminate("To run $COMMAND_VALIDATE command, the environment variable ANDROID_HOME must be set and must point to your Android SDK.")
                }

                if (mode != null) {
                    val cliMode = mode
                    if (cliMode != WORKINGCOPY) {
                        printErrorAndTerminate("""
                            '$COMMAND_VALIDATE' command with a mode, requires value '$WORKINGCOPY'.
                            To convert all recipes from '$SOURCE' mode, omit the argument
                        """.trimIndent())
                    }

                    // ensure no extra/unused values
                    validateNullArg(
                        sourceAll,
                        "'sourceAll' must not be provided for subcommand '$COMMAND_VALIDATE' and 'mode=$WORKINGCOPY'"
                    )

                    val validator = WorkingCopyValidator(Context.standalone())
                    validator.validate(
                        Path.of(
                            source
                                ?: printErrorAndTerminate("Source must not be null with subcommand '$COMMAND_VALIDATE' and 'mode=$WORKINGCOPY'")
                        )
                    )
                } else {
                    // ensure no extra/unused values
                    validateNullArg(
                        source,
                        "'source' must not be provided for subcommand '$COMMAND_VALIDATE' when not providing 'mode' argument"
                    )

                    val validator = GithubPresubmitValidator(Context.standalone())
                    validator.validateAll(
                        Path.of(
                            sourceAll
                                ?: printErrorAndTerminate("SourceAll must not be null with subcommand '$COMMAND_VALIDATE' when not providing 'mode' argument")
                        )
                    )
                }
            }
        },
        object : Subcommand(COMMAND_VALIDATE_CI, "Validate all recipes on CI - This is for Google internal usage") {
            override fun execute() {
                // ensure no extra/unused values
                validateNullArg(overwrite, "'overwrite' must not be provided for subcommand '$COMMAND_VALIDATE_CI'")
                validateNullArg(source, "'source' must not be provided for subcommand '$COMMAND_VALIDATE_CI'")
                validateNullArg(destination, "'destination' must not be provided for subcommand '$COMMAND_VALIDATE_CI'")
                validateNullArg(
                    gradleVersion,
                    "'gradleVersion' must not be provided for subcommand '$COMMAND_VALIDATE_CI'"
                )
                validateNullArg(mode, "'mode' must not be provided for subcommand '$COMMAND_VALIDATE_CI'")

                val validator = InternalCIValidator(
                    context = Context.standalone(),
                    agpVersion = agpVersion?.let { FullAgpVersion.of(it) }
                        ?: printErrorAndTerminate("'agpVersion' must not be null with subcommand '$COMMAND_VALIDATE_CI'"),
                    repoLocation = repoLocation
                        ?: printErrorAndTerminate("'repoLocation' must not be null with subcommand '$COMMAND_VALIDATE_CI'"),
                    gradlePath = gradlePath
                        ?: printErrorAndTerminate("'gradlePath' must not be null with subcommand '$COMMAND_VALIDATE_CI'"),
                )
                validator.validate(
                    sourceAll = Path.of(
                        sourceAll
                            ?: printErrorAndTerminate("'sourceAll' must not be null with subcommand '$COMMAND_VALIDATE_CI'")
                    ),
                    tmpFolder = if (tmpFolder != null) Path.of(tmpFolder) else null
                )
            }
        },
        )

    val result = parser.parse(args)

    if (result.commandName == TOOL_NAME) {
        println("Missing subcommand. Use $TOOL_NAME -h to see usage")
        exitProcess(1)
    }
}

private fun validateNullArg(arg: Any?, msg: String) {
    if (arg != null) {
        printErrorAndTerminate(msg + "\nvalue: $arg")
    }
 }

fun printErrorAndTerminate(msg: String): Nothing {
    System.err.println(msg)
    if (System.getenv("CONVERT_DEBUG") != null) {
        throw RuntimeException("error. See console output")
    }
    exitProcess(1)
}

private fun Path.isEmptyExceptForHidden(): Boolean = !Files.list(this).anyMatch { !it.name.startsWith('.') }

@OptIn(ExperimentalPathApi::class)
fun Path.deleteNonHiddenRecursively() {
    Files.list(this).filter {
        !it.name.startsWith('.')
    }.forEach {
        it.deleteRecursively()
    }
}
