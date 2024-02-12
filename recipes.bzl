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
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.1.0",
                ":kotlin_1_8_10",
                "//tools/base/build-system:gradle-8.0-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/33.0.1",
                "//tools/base/build-system:gradle-distrib-8.0",
            ],
            "jdk_version": 17,
        },
        "8.2.0": {
            "name": name + "_8_2_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.2)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.2.0",
                ":kotlin_1_8_10",
                "//tools/base/build-system:gradle-8.2-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/34.0.0",
                "//tools/base/build-system:gradle-distrib-8.2",
            ],
            "jdk_version": 17,
        },
        "8.3.0-beta01": {
            "name": name + "_8_3_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.4)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.3.0",
                ":kotlin_1_9_20",
                "//tools/base/build-system:gradle-8.4-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/34.0.0",
                "//tools/base/build-system:gradle-distrib-8.4",
            ],
            "jdk_version": 17,
        },
        "ToT": {
            "name": name,
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib)",
            "manifest_repos": [
                "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
                ":kotlin_1_9_20",
                "//tools/base/build-system:gradle-runtime-maven",
            ],
            "zip_repos": ["//tools/base/build-system:android_gradle_plugin"],
            "data": [
                "//prebuilts/studio/sdk:build-tools/latest",
                "//tools/base/build-system:gradle-distrib",
            ],
        },
    }

    for agp_version in test_scenarios:
        manifest_repos = [
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
                            "-Dversion_mappings_file=$(location :version_mappings.txt)",
                            "-Dall_tested_agp_versions=" + ",".join(test_scenarios),
                        ] +
                        (["-Djdk_version=" + str(test_scenarios[agp_version].get("jdk_version"))] if test_scenarios[agp_version].get("jdk_version") else []) +
                        (select({
                            "//tools/base/bazel:release": ["-Dagp_version=" + RELEASE_BUILD_VERSION],
                            "//conditions:default": ["-Dagp_version=" + DEV_BUILD_VERSION],
                        }) if agp_version == "ToT" else ["-Dagp_version=" + agp_version]),
            data = native.glob(
                ["recipes/" + name + "/**"],
            ) + [
                "//tools/base/build-system:android_platform_for_tests",
                "version_mappings.txt",
            ] + manifest_repos + zip_repos + repo_files + test_scenarios[agp_version]["data"] + _jdkRuntime(test_scenarios[agp_version].get("jdk_version")),
            test_class = "com.android.tools.gradle.GradleRecipeTest",
            runtime_deps = [":gradle_recipe_test"],
        )

def _jdkRuntime(jdk_version):
    if jdk_version == 17:
        return ["//prebuilts/studio/jdk/jdk17:jdk17_runtime"]
    elif jdk_version == 11:
        return ["//prebuilts/studio/jdk/jdk11:jdk11_runtime"]
    else:
        return ["//prebuilts/studio/jdk/jdk17:jdk17_runtime"]
