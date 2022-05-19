
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.1")
        classpath(kotlin("gradle-plugin", version = "1.4.32"))
        
    }
}
allprojects {
        repositories {
        google()
        jcenter()
    }
}
