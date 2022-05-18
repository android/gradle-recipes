plugins {
        id("com.android.application")
        kotlin("android")
}
import com.android.build.api.artifact.MultipleArtifact

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import javassist.ClassPool
import javassist.CtClass
import java.io.FileInputStream

abstract class AddClassesTask: DefaultTask() {

    @get:OutputFiles
    abstract val output: DirectoryProperty

    @TaskAction
    fun taskAction() {

        val pool = ClassPool(ClassPool.getDefault())


        val interfaceClass = pool.makeInterface("com.android.api.tests.SomeInterface");
        println("Adding $interfaceClass")
        interfaceClass.writeFile(output.get().asFile.absolutePath)
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
        val taskProvider = project.tasks.register<AddClassesTask>("${variant.name}AddClasses")
        variant.artifacts.use<AddClassesTask>(taskProvider)
            .wiredWith(AddClassesTask::output)
            .toAppendTo(MultipleArtifact.ALL_CLASSES_DIRS)
    }
}