# artifacts.get in Kotlin
This sample shows how to reset the variant's [com.android.build.api.variant.SigningConfig] using one
 of the DSL [com.android.build.api.dsl.SigningConfig] named element present in the android's
[signingConfigs] block.

In this example, we define 2 signing configurations : default and other.
The 'default' configuration is the default signing configuration used for all the variants signing.
However, using the Variant API, the 'flavor1Special' variant will use the 'other' signing
configuration.

## To Run
./gradlew :app:assembleFlavor1Special
expected result : "Got an APK...." message.