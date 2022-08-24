include(":app")

rootProject.name = "addCustomAsset"

pluginManagement {
    repositories {
$AGP_REPOSITORY
$PLUGIN_REPOSITORIES
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
$AGP_REPOSITORY
$DEPENDENCY_REPOSITORIES
    }
}
