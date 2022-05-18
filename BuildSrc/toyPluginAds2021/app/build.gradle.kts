plugins {
        id("com.android.application")
        kotlin("android")
}

apply<ToyPlugin>()

android { namespace = "com.android.build.example.minimal"
compileSdkVersion(29)
defaultConfig {
    minSdkVersion(21)
}
}

androidComponents {
    onVariants { variant ->
        variant.getExtension(ToyVariantExtension::class.java)?.content?.set("Hello World")
    }
}