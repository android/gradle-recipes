# Changing the minimum SDK version in Kotlin

This sample show how to change the minSdkVersion for a particular variant. Because the min SDK
version will impact the build flow, in particular how dexing is performed, it must be provided at
configuration time.

Changing the minSdkVersion through the beforeVariants API is not as straightforward as changing it in
the DSL directly and should only be done when a lot of build flavors and/or build types yield
multiple variants.

## To Run
./gradlew assembleRelease
expected result : An APK with minSdkVersion of 23