
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0-beta02")
        classpath(kotlin("gradle-plugin", version = "1.7.20-Beta"))
        
    }
}
allprojects {
        repositories {
        google()
        jcenter()
    }
}
