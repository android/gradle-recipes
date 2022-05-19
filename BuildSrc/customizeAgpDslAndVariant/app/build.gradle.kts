plugins {
        id("com.android.application")
        kotlin("android")
}

apply<ProviderPlugin>()
apply<ConsumerPlugin>()

android { namespace = "com.android.build.example.minimal"
compileSdkVersion(29)
defaultConfig {
    minSdkVersion(21)
}
    buildTypes {
        debug {
            the<BuildTypeExtension>().invocationParameters = "-debug -log"
        }
    }
}