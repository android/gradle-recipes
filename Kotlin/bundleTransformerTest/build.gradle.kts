
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.0-beta01")
        classpath(kotlin("gradle-plugin", version = "1.3.61"))
    }
}
allprojects {
    repositories {
        google()
        jcenter()
    }
}
