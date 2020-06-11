plugins {
    kotlin("jvm") version "1.3.61"
}
repositories {
    google()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.android.tools.build:gradle-api:4.1.0-beta01")
    gradleApi()
}
