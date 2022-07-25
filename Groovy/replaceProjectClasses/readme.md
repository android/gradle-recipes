# Scoped Artifacts toReplace Project classes in Groovy
This sample shows how to replace all the project classes that will be used to create the dex files.

The [onVariants] block will wire [ReplaceClassesTask]'s [output] folder to contain all the new
project classes :
`
    variant.artifacts.forScope(ScopedArtifacts.Scope.PROJECT)
        .use(taskProvider)
        .toReplace(
            ScopedArtifact.CLASSES.INSTANCE,
            { it.getOutput() }
`

## To Run
./gradlew :app:assembleDebug
expected result : all .class files are provided by the [ReplaceClassesTask] will be packaged in the
resulting APK