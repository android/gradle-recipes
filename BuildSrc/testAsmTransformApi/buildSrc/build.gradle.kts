import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins { kotlin("jvm") version "1.4.32" }

repositories {
    google()
    jcenter()
}

tasks.withType<KotlinCompile>().configureEach { kotlinOptions.apiVersion = "1.3" }

dependencies {
    implementation("com.android.tools.build:gradle-api:7.0.0")
    implementation(kotlin("stdlib"))
    gradleApi()
}

dependencies { implementation("org.ow2.asm:asm-util:7.0") }
