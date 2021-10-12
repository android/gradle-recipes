This recipe was written for the "Getting the most from the latest Android Gradle
plugin" talk at ADS 2021.

This recipe is an example of (1) modifying an AGP intermediate artifact, (2) adding
a custom element to AGP's DSL, and (3) adding a custom property to AGP's variant
API.

In this recipe, the custom AddAssetTask writes a file which is packaged in the
downstream APK. ToyExtension and ToyVariantExtension are extensions on the DSL and
Variant API, respectively, which allow the content of the extra asset to be set by
the user or by another Gradle plugin.