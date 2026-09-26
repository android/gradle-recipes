load("//tools/base/common:version.bzl", "DEV_BUILD_VERSION", "RELEASE_BUILD_VERSION")

def recipe_test_suite(
        name,
        recipes):
    """Create recipe tests and test suites.

    Args:
      name: name of the test suite
      recipes: list of relative path to recipe's metadata file
    """

    all_tests_aggregator = []

    for full_recipe_path in recipes:
        # full_recipe_path looks like recipes/myRecipe/recipe_metadata.toml, so we want to trim the first and last
        recipe_path_end = len(full_recipe_path) - 21  # removes /recipes_metadata.toml at the end
        recipe_path = full_recipe_path[8:recipe_path_end]
        recipe_test_name = _recipe_test(name, recipe_path)
        all_tests_aggregator.append(recipe_test_name)

    native.test_suite(
        name = name,
        tests = all_tests_aggregator,
    )

def _recipe_test(
        name,
        recipe_path,
        size = "enormous",
        timeout = "long"):
    """Sets up and runs a recipe test per AGP version to be tested

    Args:
      name: the name of the parent test suite
      recipe_path: path of the directory containing the recipe project
      size: size of the Java test. See
        https://docs.bazel.build/versions/master/be/common-definitions.html#test.size
      timeout: timeout of the Java test. See
        https://docs.bazel.build/versions/master/be/common-definitions.html#test.timeout
    """

    # path may contain folder segment, so sanitize it for usage as a target name
    sanitized_name = recipe_path.replace("/", "_")

    # Test scenarios keyed by AGP version. Keep in chronological order, with "ToT" (tip of tree) last.

    test_scenarios = {
        "8.1.0": {
            "name": sanitized_name + "_8_1_0",
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
            "name": sanitized_name + "_8_2_0",
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
        "8.3.1": {
            "name": sanitized_name + "_8_3_1",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.4)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.3.1",
                ":kotlin_1_9_22",
                "//tools/base/build-system:gradle-8.4-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/34.0.0",
                "//tools/base/build-system:gradle-distrib-8.4",
            ],
            "jdk_version": 17,
        },
        "8.4.0": {
            "name": sanitized_name + "_8_4_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.6)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.4.0",
                ":kotlin_1_9_22",
                "//tools/base/build-system:gradle-8.6-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/34.0.0",
                "//tools/base/build-system:gradle-distrib-8.6",
            ],
            "jdk_version": 17,
        },
        "8.5.0": {
            "name": sanitized_name + "_8_5_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.7)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.5.0",
                ":kotlin_1_9_22",
                "//tools/base/build-system:gradle-8.7-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/34.0.0",
                "//tools/base/build-system:gradle-distrib-8.7",
            ],
            "jdk_version": 17,
        },
        "8.6.0": {
            "name": sanitized_name + "_8_6_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.9)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.6.0",
                "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
                ":kotlin_1_9_22",
                "//tools/base/build-system:gradle-8.9-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/34.0.0",
                "//tools/base/build-system:gradle-distrib-8.9",
            ],
            "jdk_version": 17,
        },
        "8.7.0": {
            "name": sanitized_name + "_8_7_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.9)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.7.0",
                "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
                ":kotlin_1_9_22",
                "//tools/base/build-system:gradle-8.9-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/34.0.0",
                "//tools/base/build-system:gradle-distrib-8.9",
            ],
            "jdk_version": 17,
        },
        "8.8.0": {
            "name": sanitized_name + "_8_8_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.10.2)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.8.0",
                "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
                ":kotlin_1_9_22",
                "//tools/base/build-system:gradle-8.10.2-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/35.0.0",
                "//tools/base/build-system:gradle-distrib-8.10.2",
            ],
            "jdk_version": 17,
        },
        "8.9.0": {
            "name": sanitized_name + "_8_9_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.11.1)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.9.0",
                "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
                ":kotlin_1_9_22",
                "//tools/base/build-system:gradle-8.11.1-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/35.0.0",
                "//tools/base/build-system:gradle-distrib-8.11.1",
            ],
            "jdk_version": 17,
        },
        "8.10.1": {
            "name": sanitized_name + "_8_10_1",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.11.1)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.10.1",
                "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
                ":kotlin_2_1_20",
                "//tools/base/build-system:gradle-8.11.1-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/35.0.0",
                "//tools/base/build-system:gradle-distrib-8.11.1",
            ],
            "jdk_version": 17,
        },
        "8.11.0": {
            "name": sanitized_name + "_8_11_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.13)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.11.0",
                "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
                ":kotlin_2_1_20",
                "//tools/base/build-system:gradle-8.13-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/35.0.0",
                "//tools/base/build-system:gradle-distrib-8.13",
            ],
            "jdk_version": 17,
        },
        "8.12.0": {
            "name": sanitized_name + "_8_12_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.13)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.12.0",
                "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
                ":kotlin_2_1_20",
                "//tools/base/build-system:gradle-8.13-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/35.0.0",
                "//tools/base/build-system:gradle-distrib-8.13",
            ],
            "jdk_version": 17,
        },
        "8.13.0": {
            "name": sanitized_name + "_8_13_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-8.13)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:8.13.0",
                "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
                ":kotlin_2_1_20",
                "//tools/base/build-system:gradle-8.13-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/35.0.0",
                "//tools/base/build-system:gradle-distrib-8.13",
            ],
            "jdk_version": 17,
        },
        "9.2.0": {
            "name": sanitized_name + "_9_2_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-9.4.1)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:9.2.0",
                "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
                ":kotlin_2_3_10",
                "//tools/base/build-system:gradle-9.4.1-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/36.0.0",
                "//tools/base/build-system:gradle-distrib-9.4.1",
            ],
            "jdk_version": 17,
        },
        "9.1.0": {
            "name": sanitized_name + "_9_1_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-9.3.1)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:9.1.0",
                "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
                ":kotlin_2_2_21",
                ":kotlin_2_2_10",
                "//tools/base/build-system:gradle-9.3.1-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/36.0.0",
                "//tools/base/build-system:gradle-distrib-9.3.1",
            ],
            "jdk_version": 17,
        },
        "9.0.0": {
            "name": sanitized_name + "_9_0_0",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib-9.1.0)",
            "manifest_repos": [
                "//tools/base/build-system/previous-versions:9.0.0",
                "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
                ":kotlin_2_2_10",
                ":kotlin_2_2_0",
                "//tools/base/build-system:gradle-9.1-runtime-maven",
            ],
            "zip_repos": [],
            "data": [
                "//prebuilts/studio/sdk:build-tools/36.0.0",
                "//tools/base/build-system:gradle-distrib-9.1.0",
            ],
            "jdk_version": 17,
        },
        "ToT": {
            "name": sanitized_name + "_dev",
            "gradle_path": "$(location //tools/base/build-system:gradle-distrib)",
            "manifest_repos": [
                "//tools/base/build-system/integration-test:kotlin_gradle_plugin_prebuilts",
                ":kotlin_2_3_21",
                ":test_deps",
                "//tools/base/build-system:gradle-runtime-maven",
            ],
            "zip_repos": ["//tools/base/build-system:android_gradle_plugin"],
            "data": [
                "//prebuilts/studio/sdk:build-tools/latest",
                "//tools/base/build-system:gradle-distrib",
            ],
        },
    }

    all_tests_aggregator = []

    for agp_version in test_scenarios:
        manifest_repos = [
            "//tools/base/build-system:android_gradle_plugin_runtime_dependencies",
        ] + test_scenarios[agp_version]["manifest_repos"]
        zip_repos = test_scenarios[agp_version]["zip_repos"]
        repo_files = [repo + ".manifest" for repo in manifest_repos] + [repo + ".zip" for repo in zip_repos]

        test_name = name + "_" + test_scenarios[agp_version]["name"]
        all_tests_aggregator.append(test_name)

        native.java_test(
            name = test_name,
            size = size,
            timeout = timeout,
            jvm_flags = [
                            "-Dgradle_path=" + test_scenarios[agp_version]["gradle_path"],
                            "-Drepos=" + ",".join(["$(location " + repo_file + ")" for repo_file in repo_files]),
                            "-Dname=" + recipe_path,
                            "-Dversion_mappings_file=$(location :version_mappings.txt)",
                            "-Dall_tested_agp_versions=" + ",".join(test_scenarios),
                            "-Dconvert_debug=true",
                            "-Dvalidate_source=" + ("true" if agp_version == "ToT" else "false"),
                        ] +
                        (["-Djdk_version=" + str(test_scenarios[agp_version].get("jdk_version"))] if test_scenarios[agp_version].get("jdk_version") else []) +
                        (select({
                            "//tools/base/bazel:release": ["-Dagp_version=" + RELEASE_BUILD_VERSION],
                            "//conditions:default": ["-Dagp_version=" + DEV_BUILD_VERSION],
                        }) if agp_version == "ToT" else ["-Dagp_version=" + agp_version]),
            data = native.glob(
                [
                    "recipes/" + recipe_path + "/**",
                    "gradle-resources/**",
                ],
            ) + [
                "//tools/base/build-system:android_platform_for_agp_unit_tests",
                "version_mappings.txt",
            ] + manifest_repos + zip_repos + repo_files + test_scenarios[agp_version]["data"] + _jdkRuntime(test_scenarios[agp_version].get("jdk_version")),
            test_class = "com.android.tools.gradle.GradleRecipeTest",
            runtime_deps = [":gradle_recipe_tester"],
        )

    native.test_suite(
        name = name + "_" + sanitized_name,
        tests = all_tests_aggregator,
    )

    return name + "_" + sanitized_name

def _jdkRuntime(jdk_version):
    if jdk_version != None and jdk_version != 17:
        fail("Unsupported jdk_version: %s. Only JDK 17 is supported." % jdk_version)
    return ["//prebuilts/studio/jdk/jdk17:java_runtime"]
