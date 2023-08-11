load("//tools/base/common:version.bzl", "DEV_BUILD_VERSION", "RELEASE_BUILD_VERSION")

def recipe_test(
        name,
        size = "enormous",
        timeout = "long"):
    """Sets up and runs a recipe test per AGP version to be tested

    Args:
      name: name of the directory containing the recipe project
      size : size of the Java test. See
        https://docs.bazel.build/versions/master/be/common-definitions.html#test.size
      timeout : timeout of the Java test. See
        https://docs.bazel.build/versions/master/be/common-definitions.html#test.timeout
    """

    # Test scenarios keyed by AGP version. Keep in chronological order, with "ToT" (tip of tree) last.
    test_scenarios = {
        "8.1.0": {
            "name": name + "_8_1_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.0)",
            "manifest_repos": ["//tools/base/build-system/previous-versions:8.1.0"],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/33.0.1",
                "//tools/base/build-system:android_platform_for_tests",
                "//tools/base/build-system:gradle-distrib-8.0",
            ],
        },
        "ToT": {
            "name": name,
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib)",
            "manifest_repos": [],
            "zip_repos": ["//tools/base/build-system:android_gradle_plugin"],
            "data": [
                "//prebuilts/studio/sdk:build-tools/33.0.1",
                "//prebuilts/studio/sdk:build-tools/latest",
                "//tools/base/build-system:android_platform_for_tests",
                "//tools/base/build-system:gradle-distrib",
                "//tools/base/build-system:gradle-distrib-8.0",
                "version_mappings.txt",
            ],
        },
    }

    for agp_version in test_scenarios:
        manifest_repos = [
            "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
            "//tools/base/build-system:android_gradle_plugin_runtime_dependencies",
        ] + test_scenarios[agp_version]["manifest_repos"]
        zip_repos = test_scenarios[agp_version]["zip_repos"]
        repo_files = [repo + ".manifest" for repo in manifest_repos] + [repo + ".zip" for repo in zip_repos]

        native.java_test(
            name = test_scenarios[agp_version]["name"],
            size = size,
            timeout = timeout,
            jvm_flags = [
                "-Dgradle_path=" + test_scenarios[agp_version]["gradle_path"],
                "-Drepos=" + ",".join(["$(location " + repo_file + ")" for repo_file in repo_files]),
                "-Dname=" + name,
            ] + (select({
                "//tools/base/bazel:release": ["-Dagp_version=" + RELEASE_BUILD_VERSION],
                "//conditions:default": ["-Dagp_version=" + DEV_BUILD_VERSION],
            }) if agp_version == "ToT" else ["-Dagp_version=" + agp_version]) + ([
                "-Dversion_mappings_file=$(location :version_mappings.txt)",
                "-Dtested_agp_versions=" + ",".join(test_scenarios),
                "-Dtested_gradle_paths=" + ",".join([test_scenarios[key]["gradle_path"] for key in test_scenarios]),
            ] if agp_version == "ToT" else []),
            data = native.glob(
                ["recipes/" + name + "/**"],
            ) + manifest_repos + zip_repos + repo_files + test_scenarios[agp_version]["data"],
            test_class = "com.android.tools.gradle.GradleRecipeTest",
            runtime_deps = [":gradle_recipe_test"],
        )
