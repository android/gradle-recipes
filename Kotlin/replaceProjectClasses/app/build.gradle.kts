 plugins {
         id("com.android.application")
         kotlin("android")
}
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.api.artifact.ScopedArtifact

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import javassist.ClassPool
import javassist.CtClass
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import java.io.File
import java.util.jar.JarFile
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

abstract class ReplaceClassesTask: DefaultTask() {

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {

        val pool = ClassPool(ClassPool.getDefault())

        JarOutputStream(BufferedOutputStream(FileOutputStream(
             output.get().asFile
        ))).use  {
             val interfaceClass = pool.makeInterface("com.android.api.tests.SomeInterface");
             println("Adding $interfaceClass")
             it.putNextEntry(JarEntry("com/android/api/tests/SomeInterface.class"))
             it.write(interfaceClass.toBytecode())
             it.closeEntry()
        }
    }
}
android {
    namespace = "com.android.build.example.minimal"
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
    }
}

androidComponents {
    onVariants { variant ->
        val taskProvider = project.tasks.register<ReplaceClassesTask>("${variant.name}ModifyClasses")
         variant.artifacts.forScope(ScopedArtifacts.Scope.PROJECT)
             .use(taskProvider)
             .toReplace(
                 ScopedArtifact.CLASSES,
                 ReplaceClassesTask::output
             )
    }
}