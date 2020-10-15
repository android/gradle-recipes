
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-alpha13")
        classpath(kotlin("gradle-plugin", version = "1.4.10"))
    }
}
allprojects {
        repositories {
        google()
        jcenter()
    }
}
