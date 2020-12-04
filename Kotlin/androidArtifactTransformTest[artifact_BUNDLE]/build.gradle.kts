
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha01")
        classpath(kotlin("gradle-plugin", version = "1.4.10"))
    }
}
allprojects {
        repositories {
        google()
        jcenter()
    }
}
