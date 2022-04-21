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

package com.google.android.gradle_recipe.converter

import com.google.android.gradle_recipe.converter.converters.RecipeConverter
import com.google.android.gradle_recipe.converter.converters.RecursiveConverter
import com.google.android.gradle_recipe.converter.converters.convertStringToMode
import com.google.android.gradle_recipe.converter.validators.GithubPresubmitValidator
import com.google.android.gradle_recipe.converter.validators.InternalCIValidator
import com.google.android.gradle_recipe.converter.validators.WorkingCopyValidator
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import java.nio.file.Path

/**
 * The main entry to the Converter, parser the command line arguments, and calls
 * the relevant classes to perform the work
 */
fun main(args: Array<String>) {
    val parser = ArgParser("Gradle Recipe Converter")

    val action by parser.option(
        ArgType.Choice(listOf("convert", "validate"), { it }), shortName = "al", description = "Recipe action"
    ).default("convert")

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

    parser.parse(args)

    if (action == "convert") {
        if (source != null) {
            RecipeConverter(
                agpVersion = agpVersion,
                repoLocation = repoLocation,
                gradleVersion = gradleVersion,
                gradlePath = gradlePath,
                mode = convertStringToMode(mode),
                overwrite = overwrite
            ).convert(
                source = Path.of(source ?: error("source must be specified")),
                destination = Path.of(destination ?: error("destination must be specified"))
            )
        } else {
            RecursiveConverter(
                agpVersion = agpVersion,
                repoLocation = repoLocation,
                gradleVersion = gradleVersion,
                gradlePath = gradlePath,
                overwrite = overwrite
            ).convertAllRecipes(
                sourceAll = Path.of(sourceAll ?: error("sourceAll must be specified")),
                destination = Path.of(destination ?: error("destination must be specified"))
            )
        }
    } else if (action == "validate") {
        if (agpVersion == null) {
            if (mode != null) {
                val cliMode = convertStringToMode(mode)
                if (cliMode != RecipeConverter.Mode.COPY) {
                    error("The mode value should be either \"copy\" or nothing")
                }

                val validator = WorkingCopyValidator()
                validator.validate(
                    Path.of(source ?: error("Source can't be null"))
                )
            } else {
                val validator = GithubPresubmitValidator()
                validator.validateAll(
                    Path.of(sourceAll ?: error("SourceAll can't be null"))
                )
            }
        } else {
            val validator = InternalCIValidator(
                agpVersion = agpVersion ?: error("agpVersion can't be null"),
                repoLocation = repoLocation ?: error("repoLocation can't be null"),
                gradlePath = gradlePath ?: error("gradlePath can't be null")
            )
            validator.validate(
                sourceAll = Path.of(sourceAll ?: error("sourceAll can't be null")),
                tmpFolder = if (tmpFolder != null) Path.of(tmpFolder) else null
            )
        }
    }
}
