import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    maven("/home/jedo/src/studio-4.2-dev/prebuilts/tools/common/m2/repository")
}

repositories {
    google()
    mavenCentral()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.apiVersion = "1.3"
}

dependencies {
    implementation("com.android.tools.build:gradle-api:4.2.0")
    implementation(kotlin("stdlib"))
    gradleApi()
}
dependencies {
    implementation("org.ow2.asm:asm-util:7.0")
}