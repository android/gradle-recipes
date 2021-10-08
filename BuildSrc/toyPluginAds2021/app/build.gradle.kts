plugins {
        id("com.android.application")
        kotlin("android")
}

apply<ToyPlugin>()

android { compileSdkVersion(29)
defaultConfig {
    minSdkVersion(21)
    targetSdkVersion(29)
}
}

androidComponents {
    onVariants { variant ->
        variant.getExtension(ToyVariantExtension::class.java)?.content?.set("Hello World")
    }
}