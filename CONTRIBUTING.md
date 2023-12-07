`studio-main` branch contains the source recipes.

A converter tool is used to convert source recipes into buildable projects that are
stored in a AGP Version related branch. So for examples, buildable recipes that use
AGP 8.1 will be found in the agp-8.1 branch.

The `agp-` branches are readonly branches, if you want to make changes to a recipe,
you must do it in the studio-main branch and let the github workflow recreate the
recipe in the `agp-` branches from the modified sources.

This project structure is as follows :


| Folder           | Contents                                                        |
| -----------------|-----------------------------------------------------------------|
| convert-tool     | tool to convert source recipes into buildable projects          |
| recipes          | source recipes                                                  |
| templates        | template recipes to help creating new recipes                   |

The mapping between AGP versions and Gradle versions is driven by this [version_mappings.txt](version_mappings.txt),
the line format is `AGP_VERSION;GRADLE_VERSION` and each line will create a branch using the `agp-${AGP Major.Minor}`
(so 8.1.0-rc1) will create `agp-8.1` branch.

| Branch  | AGP and Gradle Versions |
|---------|-------------------------|
| agp-8-1 | AGP 8.1.0-rc1 and Gradle 8.0 |
| agp-8-2 | AGP 8.2.0-alpha13 and Gradle 8.1 |

