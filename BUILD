load("//tools/base/bazel:kotlin.bzl", "kotlin_library")
load(":recipes.bzl", "recipe_test")

kotlin_library(
    name = "convert_tool",
    srcs = glob([
        "convert-tool/app/src/main/kotlin/com/google/android/gradle_recipe/converter/**/*.kt",
    ]),
    deps = [
        "@maven//:com.github.rising3.semver",
        "@maven//:com.google.guava.guava",
        "@maven//:org.gradle.gradle-tooling-api",
        "@maven//:org.jetbrains.kotlinx.kotlinx-cli-jvm",
        "@maven//:org.tomlj.tomlj",
    ],
)

kotlin_library(
    name = "gradle_recipe_test",
    testonly = 1,
    srcs = glob(["testSrc/com/android/tools/gradle/**/*.kt"]),
    visibility = ["//visibility:public"],
    deps = [
        ":convert_tool",
        "//tools/base/bazel:gradle",
        "@maven//:junit.junit",
    ],
)

recipe_test(
    name = "addBuildTypeUsingDslFinalize",
)

recipe_test(
    name = "addCustomAsset",
)

recipe_test(
    name = "addCustomBuildConfigFields",
)

recipe_test(
    name = "addMultipleArtifact",
)

recipe_test(
    name = "allProjectsApkAction",
)

recipe_test(
    name = "appendToMultipleArtifact",
)

recipe_test(
    name = "extendingAgp",
)

recipe_test(
    name = "getMultipleArtifact",
)

recipe_test(
    name = "getScopedArtifacts",
)

recipe_test(
    name = "getSingleArtifact",
)

recipe_test(
    name = "perVariantManifestPlaceholder",
)

recipe_test(
    name = "variantDependencySubstitutionTest",
)

recipe_test(
    name = "selectVariants",
)

recipe_test(
    name = "createSingleArtifact",
)

recipe_test(
    name = "addCustomSourceFolders",
)

recipe_test(
    name = "transformManifest",
)
