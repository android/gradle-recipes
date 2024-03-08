plugins {
        id("com.android.application")
        kotlin("android")
        kotlin("android.extensions")
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

abstract class ModifyClassesTask: DefaultTask() {

    @get:InputFiles
    abstract val allClasses: ListProperty<Directory>

    @get:OutputFiles
    abstract val output: DirectoryProperty

    @TaskAction
    fun taskAction() {

        val pool = ClassPool(ClassPool.getDefault())

        allClasses.get().forEach { directory ->
            println("Directory : ${directory.asFile.absolutePath}")
            directory.asFile.walk().filter(File::isFile).forEach { file ->
                if (file.name == "SomeSource.class") {
                    println("File : ${file.absolutePath}")
                    val interfaceClass = pool.makeInterface("com.android.api.tests.SomeInterface");
                    println("Adding $interfaceClass")
                    interfaceClass.writeFile(output.get().asFile.absolutePath)
                    FileInputStream(file).use {
                        val ctClass = pool.makeClass(it);
                        ctClass.addInterface(interfaceClass)
                        val m = ctClass.getDeclaredMethod("toString");
                        if (m != null) {
                            m.insertBefore("{ System.out.println(\"Some Extensive Tracing\"); }");
                        }
                        ctClass.writeFile(output.get().asFile.absolutePath)
                    }
                }
            }
        }
    }
}
android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
    }
}

androidComponents {
    onVariants { variant ->
        val taskProvider = project.tasks.register<ModifyClassesTask>("${variant.name}ModifyClasses")
        variant.artifacts.use<ModifyClassesTask>(taskProvider)
            .wiredWith(ModifyClassesTask::allClasses, ModifyClassesTask::output)
            .toTransform(MultipleArtifact.ALL_CLASSES_DIRS)
    }
}