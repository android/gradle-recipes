
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.0")
        classpath(kotlin("gradle-plugin", version = "1.5.31"))
        
    }
}
allprojects {
        repositories {
        google()
        jcenter()
    }
}
