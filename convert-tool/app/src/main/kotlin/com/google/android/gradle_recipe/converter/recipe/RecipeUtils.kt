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

package com.google.android.gradle_recipe.converter.recipe

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isHidden

/** Visitor function, recursively traverses rootFolder with recipes and
 *  activates recipeCallback for each found recipe.
 */
fun visitRecipes(rootFolder: Path, recipeCallback: (Path) -> Unit) {
    if (!rootFolder.exists()) {
        error("the source $rootFolder folder doesn't exist")
    }

    Files.walk(rootFolder).filter { currentPath: Path ->
        Files.isDirectory(currentPath)
                && currentPath != rootFolder
                && !currentPath.isHidden()
                && isRecipeFolder(currentPath)
    }.forEach { recipeFolder: Path ->
        recipeCallback(recipeFolder)
    }
}