plugins {
    kotlin("jvm") version "1.3.72"
}

repositories {
    google()
    jcenter()
}

dependencies {
    implementation("com.android.tools.build:gradle-api:4.1.0-beta01")
    implementation(kotlin("stdlib"))
    gradleApi()
}
dependencies {
    implementation("com.android.tools.build:gradle:4.1.0-beta01")
}