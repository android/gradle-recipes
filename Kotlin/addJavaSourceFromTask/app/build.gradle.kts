plugins {
        id("com.android.application")
        kotlin("android")
        kotlin("android.extensions")
}
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal

abstract class AddJavaSources: DefaultTask() {

    @get:OutputDirectory
    abstract val outputFolder: DirectoryProperty

    @TaskAction
    fun taskAction() {
        val outputFile = File(outputFolder.asFile.get(), "com/foo/Bar.java")
        outputFile.parentFile.mkdirs()
        outputFile.writeText("""
        package com.foo;

        public class Bar {
            public String toString() {
                return "a Bar instance";
            }
        }
        """)
    }
}

abstract class DisplayAllSources: DefaultTask() {

    @get:InputFiles
    abstract val sourceFolders: ListProperty<Directory>

    @TaskAction
    fun taskAction() {

        sourceFolders.get().forEach { directory ->
            println(">>> Got a Directory $directory")
            println("<<<")
        }
    }
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
    }
}

androidComponents {
    onVariants { variant ->
        val addSourceTaskProvider =  project.tasks.register<AddJavaSources>("${variant.name}AddSources") {
            outputFolder.set(project.layout.buildDirectory.dir("gen"))
        }

        variant.sources.java.add(addSourceTaskProvider, AddJavaSources::outputFolder)

        project.tasks.register<DisplayAllSources>("${variant.name}DisplayAllSources") {
            sourceFolders.set(variant.sources.java.all)
        }
    }
}