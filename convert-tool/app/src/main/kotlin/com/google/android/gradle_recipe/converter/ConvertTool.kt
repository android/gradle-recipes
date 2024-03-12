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
import com.google.android.gradle_recipe.converter.context.DefaultContext
import com.google.android.gradle_recipe.converter.converters.FullAgpVersion
import com.google.android.gradle_recipe.converter.converters.RecipeConverter
import com.google.android.gradle_recipe.converter.converters.RecipeConverter.Mode
import com.google.android.gradle_recipe.converter.converters.RecipeConverter.Mode.RELEASE
import com.google.android.gradle_recipe.converter.converters.RecipeConverter.Mode.SOURCE
import com.google.android.gradle_recipe.converter.converters.RecipeConverter.Mode.WORKINGCOPY
import com.google.android.gradle_recipe.converter.converters.RecursiveConverter
import com.google.android.gradle_recipe.converter.validators.GithubPresubmitValidator
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
    val agpVersion by parser.option(ArgType.String, shortName = "a", description = "AGP version")
    val repoLocation by parser.option(
        ArgType.String,
        shortName = "rl",
        description = "Repo location: the location of the repo to replace ${"$"}AGP_REPOSITORY in the recipe(s)."
    )
    val gradleVersion by parser.option(ArgType.String, shortName = "gv", description = "Gradle version")
    val gradlePath by parser.option(
        ArgType.String,
        shortName = "gp",
        description = "Gradle path: the value used for distributionUrl in gradle-wrapper.properties "
    )
    val javaHome by parser.option(ArgType.String, description = "Java home used for validation")
    val androidHome by parser.option(ArgType.String, description = "ANDROID_HOME used for validation")
    val mode by parser.option(
        ArgType.Choice<Mode>(),
        shortName = "m",
        description = "Conversion Mode",
    )
    val gradleRecipesFolder by parser.option(
        ArgType.String,
        description = "The location of the gradle-recipes folder. If not specified, the tool will assume a location."
    )
    val ci by parser.option(
        ArgType.Boolean,
        description = "Whether validation is being done on CI - This is for Google internal usage."
    ).default(false)

    val context: Context by lazy {
        DefaultContext.createDefaultContext(
            rootFolder = gradleRecipesFolder?.let { Path.of(it) },
            ci,
            repoLocation,
            gradlePath,
            javaHome,
            androidHome
        )
    }

    parser.subcommands(
        object : Subcommand(
            COMMAND_CONVERT,
            "Convert one or more recipes from one state to the other (default mode is $RELEASE)"
        ) {
            override fun execute() {
                // ensure no extra/unused values
                validateNullArg(
                    javaHome,
                    "'javaHome' must not be provided for subcommand '$COMMAND_CONVERT'"
                )
                validateNullArg(
                    androidHome,
                    "'androidHome' must not be provided for subcommand '$COMMAND_CONVERT'"
                )
                val finalSource = source
                val finalSourceAll = sourceAll

                val destinationPath: Path =
                    Path.of(destination ?: printErrorAndTerminate("destination must be specified"))

                if (!destinationPath.isDirectory()) {
                    printErrorAndTerminate("Folder does not exist: ${destinationPath.toAbsolutePath()}")
                }

                if (finalSource != null) {
                    RecipeConverter(
                        context = context,
                        agpVersion = agpVersion?.let { FullAgpVersion.of(it) },
                        gradleVersion = gradleVersion,
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
                        context = context,
                        agpVersion = agpVersion?.let { FullAgpVersion.of(it) },
                        gradleVersion = gradleVersion,
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
                validateNullArg(gradleVersion, "'gradleVersion' must not be provided for subcommand '$COMMAND_VALIDATE'")

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

                    val validator =
                        WorkingCopyValidator(context, agpVersion?.let { FullAgpVersion.of(it) })
                    validator.validate(
                        Path.of(
                            source
                                ?: printErrorAndTerminate("Source must not be null with subcommand '$COMMAND_VALIDATE' and 'mode=$WORKINGCOPY'")
                        )
                    )
                } else {
                    // TODO(b/328820202) modify this else block to check a single recipe in source mode.
                    // ensure no extra/unused values
                    validateNullArg(
                        source,
                        "'source' must not be provided for subcommand '$COMMAND_VALIDATE' when not providing 'mode' argument"
                    )
                    validateNullArg(
                        agpVersion,
                        "'agpVersion' must not be provided for subcommand '$COMMAND_VALIDATE' when not providing 'mode' argument"
                    )
                    validateNullArg(
                        repoLocation,
                        "'repoLocation' must not be provided for subcommand '$COMMAND_VALIDATE' when not providing 'mode' argument"
                    )
                    validateNullArg(
                        gradlePath,
                        "'gradlePath' must not be provided for subcommand '$COMMAND_VALIDATE' when not providing 'mode' argument"
                    )
                    validateNullArg(
                        javaHome,
                        "'javaHome' must not be provided for subcommand '$COMMAND_VALIDATE' when not providing 'mode' argument"
                    )
                    validateNullArg(
                        androidHome,
                        "'androidHome' must not be provided for subcommand '$COMMAND_VALIDATE' when not providing 'mode' argument"
                    )

                    val validator = GithubPresubmitValidator(context)
                    validator.validateAll(
                        Path.of(
                            sourceAll
                                ?: printErrorAndTerminate("SourceAll must not be null with subcommand '$COMMAND_VALIDATE' when not providing 'mode' argument")
                        )
                    )
                }
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
    if (System.getProperty("convert_debug") != null) {
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
