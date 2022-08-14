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

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

abstract class ExamplePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // attach the BuildTypeExtension to each elements returned by the
        // android buildTypes API.
        val android = project.extensions.getByType(ApplicationExtension::class.java)
        android.buildTypes.forEach {
            (it as ExtensionAware).extensions.add("exampleDsl", BuildTypeExtension::class.java)
        }

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        // hook up task configuration on the variant API.
        androidComponents.onVariants { variant ->
            // get the associated DSL BuildType element from the variant name
            val buildTypeDsl = android.buildTypes.getByName(variant.name)
            // find the extension on that DSL element.
            val buildTypeExtension =
                (buildTypeDsl as ExtensionAware).extensions.findByName("exampleDsl")
                    as BuildTypeExtension
            // create and configure the Task using the extension DSL values.
            project.tasks.register(variant.name + "Example", ExampleTask::class.java) { task ->
                task.parameters.set(buildTypeExtension.invocationParameters ?: "")
            }
        }
    }
}
