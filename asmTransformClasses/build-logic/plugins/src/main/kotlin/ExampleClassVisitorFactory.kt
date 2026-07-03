/*
 * Copyright (C) 2024 The Android Open Source Project
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

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.InstrumentationParameters
import java.io.PrintWriter
import java.io.File
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.util.TraceClassVisitor
import org.gradle.api.tasks.Input
import org.gradle.api.provider.Property

/**
 * This is an example of a class visitor factory. In this recipe it is used in the transformClassesWith API to be
 * performed on the classes specified in the instrumentation scope.
 */
abstract class ExampleClassVisitorFactory : AsmClassVisitorFactory<ExampleClassVisitorFactory.ExampleParams> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        // This will transform the method names of the classes
        return ClassMethodVisitor(
            instrumentationContext.apiVersion.get(),
            nextClassVisitor,
            parameters.get().newMethodName.get()
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.className.contains("SomeSource")
    }

    interface ExampleParams : InstrumentationParameters {
        @get:Input
        val newMethodName: Property<String>
    }
}
