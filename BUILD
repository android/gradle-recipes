load("//tools/base/bazel:kotlin.bzl", "kotlin_library", "kotlin_test")
load("//tools/base/bazel:maven.bzl", "maven_repository")
load("//tools/base/build-system/integration-test:common-dependencies.bzl", "KGP_1_8_10", "KGP_1_9_20")
load(":recipes.bzl", "recipe_test")

kotlin_library(
    name = "convert_tool",
    srcs = glob([
        "convert-tool/app/src/main/kotlin/**/*.kt",
    ]),
    lint_baseline = "lint_baseline.xml",
    deps = [
        "@maven//:com.google.guava.guava",
        "@maven//:com.squareup.okhttp3.okhttp",
        "@maven//:org.gradle.gradle-tooling-api",
        "@maven//:org.jetbrains.kotlinx.kotlinx-cli-jvm",
        "@maven//:org.tomlj.tomlj",
    ],
)

kotlin_test(
    name = "convert_tool_tests",
    srcs = glob([
        "convert-tool/app/src/test/kotlin/**/*.kt",
    ]),
    jvm_flags = ["-Dtest.suite.jar=convert_tool_tests.jar"],
    test_class = "com.android.testutils.JarTestSuite",
    deps = [
        ":convert_tool",
        "//tools/base/testutils:tools.testutils",
        "@maven//:com.google.truth.truth",
        "@maven//:junit.junit",
    ],
)

kotlin_library(
    name = "gradle_recipe_test",
    testonly = 1,
    srcs = glob(["convert-tool/integTest/src/main/kotlin/**/*.kt"]),
    visibility = ["//visibility:public"],
    deps = [
        ":convert_tool",
        "//tools/base/bazel:gradle",
        "@maven//:junit.junit",
    ],
)

# for testing against older KGP
maven_repository(
    name = "kotlin_1_9_20",
    artifacts = KGP_1_9_20,
    visibility = [":__subpackages__"],
)

maven_repository(
    name = "kotlin_1_8_10",
    artifacts = KGP_1_8_10,
    visibility = [":__subpackages__"],
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

recipe_test(
    name = "workerEnabledTransformation",
)

recipe_test(
    name = "transformAllClasses",
)

recipe_test(
    name = "onVariants",
)

recipe_test(
    name = "legacyTaskBridging",
)

recipe_test(
    name = "testFixtures",
)

recipe_test(
    name = "asmTransformClasses",
)

recipe_test(
    name = "transformDirectory",
)

recipe_test(
    name = "transformMultiple",
)
