
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.0")
        classpath(kotlin("gradle-plugin", version = "1.5.31"))
        classpath("org.javassist:javassist:3.22.0-GA")
    }
}
allprojects {
        repositories {
        google()
        jcenter()
    }
}
