import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
}

repositories {
    google()
    jcenter()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.apiVersion = "1.3"
}

dependencies {
    implementation("com.android.tools.build:gradle-api:7.4.0")
    implementation(kotlin("stdlib"))
    gradleApi()
}
