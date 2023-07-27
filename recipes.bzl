load("//tools/base/common:version.bzl", "DEV_BUILD_VERSION", "RELEASE_BUILD_VERSION")

def recipe_test(
        name,
        size = "enormous",
        timeout = "long"):
    """Sets up and runs a recipe test

    Args:
      name: name of the directory containing the recipe project
      size : size of the Java test. See
        https://docs.bazel.build/versions/master/be/common-definitions.html#test.size
      timeout : timeout of the Java test. See
        https://docs.bazel.build/versions/master/be/common-definitions.html#test.timeout
    """
    manifest_repos = [
        "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
        "//tools/base/build-system:android_gradle_plugin_runtime_dependencies",
    ]
    zip_repos = ["//tools/base/build-system:android_gradle_plugin"]
    repo_files = [repo + ".manifest" for repo in manifest_repos] + [repo + ".zip" for repo in zip_repos]

    native.java_test(
        name = name,
        size = size,
        timeout = timeout,
        jvm_flags = [
            "-Dgradle_path=$(location //tools/base/build-system:gradle-distrib)",
            "-Drepos=" + ",".join(["$(location " + repo_file + ")" for repo_file in repo_files]),
            "-Dname=" + name,
        ] + (select({
            "//tools/base/bazel:release": ["-Dagp_version=" + RELEASE_BUILD_VERSION],
            "//conditions:default": ["-Dagp_version=" + DEV_BUILD_VERSION],
        })),
        data = manifest_repos + zip_repos + repo_files + [
            "//tools/base/build-system:gradle-distrib",
            "//tools/base/build-system:android_platform_for_tests",
            "//prebuilts/studio/sdk:build-tools/latest",
        ] + native.glob(["recipes/" + name + "/**"]),
        test_class = "com.android.tools.gradle.GradleRecipeTest",
        runtime_deps = [":gradle_recipe_test"],
    )
