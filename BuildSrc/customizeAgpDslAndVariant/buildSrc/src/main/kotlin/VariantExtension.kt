/*
 * Copyright (C) 2019 The Android Open Source Project
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
/** Simple Variant scoped extension interface that will be attached to the AGP variant object. */
import org.gradle.api.provider.Property

interface VariantExtension {
    /**
     * the parameters is declared a Property<> so other plugins can declare a task providing this
     * value that will then be determined at execution time.
     */
    val parameters: Property<String>
}
