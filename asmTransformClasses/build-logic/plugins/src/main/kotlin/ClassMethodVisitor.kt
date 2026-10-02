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

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class ClassMethodVisitor(
    apiVersion: Int,
    cv: ClassVisitor,
    private val newMethodName: String
) : ClassVisitor(apiVersion, cv) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (name == "someMethod") {
            return super.visitMethod(access, newMethodName, descriptor, signature, exceptions)
        }
         return super.visitMethod(access, name, descriptor, signature, exceptions)
    }
}