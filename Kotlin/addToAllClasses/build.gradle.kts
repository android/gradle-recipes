
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha13")
        classpath(kotlin("gradle-plugin", version = "1.4.32"))
        classpath("org.javassist:javassist:3.22.0-GA")
    }
}
allprojects {
        repositories {
        google()
        jcenter()
    }
}
