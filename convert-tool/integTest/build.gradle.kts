plugins {
    alias(libs.plugins.kotlin)
    `java-library`
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.stdlib)

    implementation(project(":app"))
    runtimeOnly(libs.slf4j)

    // test dependencies
    implementation(libs.kotlin.test)
    implementation(libs.kotlin.test.junit)
    implementation(libs.google.truth)
}
