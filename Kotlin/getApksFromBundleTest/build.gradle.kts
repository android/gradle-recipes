
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.0-alpha08")
        classpath(kotlin("gradle-plugin", version = "1.7.0"))
        
    }
}
allprojects {
        repositories {
        google()
        jcenter()
    }
}
