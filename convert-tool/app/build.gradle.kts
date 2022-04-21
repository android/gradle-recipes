// workaround for https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin)
    application
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.stdlib)

    // app dependencies
    implementation(libs.guava)
    implementation(libs.kotlinx.cli)
    implementation(libs.tomlj)
    implementation(libs.semver)
    implementation(libs.gradle.api)
    runtimeOnly(libs.slf4j)

    // test dependencies
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.google.truth)
}

application {
    // Define the main class for the application.
    mainClass.set("com.google.android.gradle_recipe.converter.ConvertToolKt")
}

base {
    archivesName.set("recipes-converter")
}

tasks {
    register("standaloneJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes["Main-Class"] = "com.google.android.gradle_recipe.converter.ConvertToolKt"
        }
        from(configurations.runtimeClasspath.get()
            .onEach { println("add from dependencies: ${it.name}") }
            .map { if (it.isDirectory) it else zipTree(it) })
        val sourcesMain = sourceSets.main.get()
        sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
        from(sourcesMain.output)
    }
}