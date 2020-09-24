
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.0-rc03")
        classpath(kotlin("gradle-plugin", version = "1.3.72"))
    }
}
allprojects {
        repositories {
            google()
            jcenter()
    }
}
