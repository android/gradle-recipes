# Recipes for AGP version `8.1`
This branch contains recipes compatible with AGP 8.1. If you want to find recipes
for other AGP versions, switch to the corresponding `agp-*` branch.

This branch is read only. Contributions are only accepted on the `studio-main` branch. See `CONTRIBUTION.md`
there.
# Recipes Index
Index is organized in categories, offering different ways to reach the recipe you want.
## Themes
* Android Assets - [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset)
* Android Manifest - [perVariantManifestPlaceholder](perVariantManifestPlaceholder), [createSingleArtifact](createSingleArtifact), [transformManifest](transformManifest)
* Artifact API - [workerEnabledTransformation](workerEnabledTransformation), [createSingleArtifact](createSingleArtifact), [transformDirectory](transformDirectory), [getScopedArtifacts](getScopedArtifacts), [appendToMultipleArtifact](appendToMultipleArtifact), [getMultipleArtifact](getMultipleArtifact), [addMultipleArtifact](addMultipleArtifact), [getSingleArtifact](getSingleArtifact), [transformAllClasses](transformAllClasses), [transformManifest](transformManifest)
* DSL - [extendingAgp](extendingAgp), [addBuildTypeUsingDslFinalize](addBuildTypeUsingDslFinalize)
* Dependency Resolution - [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* Sources - [addCustomSourceFolders](addCustomSourceFolders), [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset)
## Plugin Features
* TestFixtures - [testFixtures](testFixtures)
## APIs
* AndroidComponentsExtension.beforeVariants() - [selectVariants](selectVariants)
* AndroidComponentsExtension.onVariants() - [addCustomSourceFolders](addCustomSourceFolders), [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset), [addCustomBuildConfigFields](addCustomBuildConfigFields), [workerEnabledTransformation](workerEnabledTransformation), [variantOutput](variantOutput), [perVariantManifestPlaceholder](perVariantManifestPlaceholder), [onVariants](onVariants), [extendingAgp](extendingAgp), [variantDependencySubstitutionTest](variantDependencySubstitutionTest), [createSingleArtifact](createSingleArtifact), [transformDirectory](transformDirectory), [getScopedArtifacts](getScopedArtifacts), [asmTransformClasses](asmTransformClasses), [appendToMultipleArtifact](appendToMultipleArtifact), [allProjectsApkAction](allProjectsApkAction), [getMultipleArtifact](getMultipleArtifact), [addMultipleArtifact](addMultipleArtifact), [getSingleArtifact](getSingleArtifact), [transformAllClasses](transformAllClasses), [transformManifest](transformManifest)
* AndroidComponentsExtension.registerExtension() - [extendingAgp](extendingAgp)
* AndroidComponentsExtension.selector() - [variantOutput](variantOutput), [allProjectsApkAction](allProjectsApkAction), [selectVariants](selectVariants)
* ApplicationVariant.applicationId - [onVariants](onVariants)
* ApplicationVariant.outputs - [variantOutput](variantOutput)
* ArtifactTransformationRequest - [workerEnabledTransformation](workerEnabledTransformation)
* Artifacts.add() - [addMultipleArtifact](addMultipleArtifact)
* Artifacts.forScope() - [getScopedArtifacts](getScopedArtifacts), [transformAllClasses](transformAllClasses)
* Artifacts.get() - [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset), [variantOutput](variantOutput), [perVariantManifestPlaceholder](perVariantManifestPlaceholder), [transformDirectory](transformDirectory), [appendToMultipleArtifact](appendToMultipleArtifact), [allProjectsApkAction](allProjectsApkAction), [addMultipleArtifact](addMultipleArtifact), [getSingleArtifact](getSingleArtifact), [transformManifest](transformManifest)
* Artifacts.getAll() - [getMultipleArtifact](getMultipleArtifact)
* Artifacts.use() - [workerEnabledTransformation](workerEnabledTransformation), [createSingleArtifact](createSingleArtifact), [transformDirectory](transformDirectory), [appendToMultipleArtifact](appendToMultipleArtifact), [transformManifest](transformManifest)
* BuildConfigField() - [addCustomBuildConfigFields](addCustomBuildConfigFields)
* BuiltArtifact - [workerEnabledTransformation](workerEnabledTransformation)
* CanMinifyAndroidResourcesBuilder.shrinkResources - [selectVariants](selectVariants)
* CanMinifyCodeBuilder.isMinifyEnabled - [selectVariants](selectVariants)
* Component.artifacts - [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset), [variantOutput](variantOutput), [createSingleArtifact](createSingleArtifact), [transformDirectory](transformDirectory), [getScopedArtifacts](getScopedArtifacts), [appendToMultipleArtifact](appendToMultipleArtifact), [getMultipleArtifact](getMultipleArtifact), [addMultipleArtifact](addMultipleArtifact), [transformManifest](transformManifest)
* Component.compileConfiguration - [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* Component.runtimeConfiguration - [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* Component.sources - [addCustomSourceFolders](addCustomSourceFolders), [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset)
* Configuration.resolutionStrategy - [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* DslExtension.Builder.build() - [extendingAgp](extendingAgp)
* DslExtension.Builder.extendBuildTypeWith() - [extendingAgp](extendingAgp)
* DslExtension.Builder.extendProductFlavorWith() - [extendingAgp](extendingAgp)
* DslExtension.Builder.extendProjectWith() - [extendingAgp](extendingAgp)
* DslLifecycle.finalizeDsl() - [addBuildTypeUsingDslFinalize](addBuildTypeUsingDslFinalize)
* GeneratesApk.applicationId - [onVariants](onVariants)
* Gradle.beforeProject() - [allProjectsApkAction](allProjectsApkAction)
* HasUnitTestBuilder.enableUnitTest - [selectVariants](selectVariants)
* InAndOutDirectoryOperationRequest.toTransform() - [transformDirectory](transformDirectory)
* InAndOutDirectoryOperationRequest.toTransformMany() - [workerEnabledTransformation](workerEnabledTransformation)
* InAndOutFileOperationRequest.toTransform() - [transformManifest](transformManifest)
* Instrumentation.transformClassesWith() - [asmTransformClasses](asmTransformClasses)
* MapProperty.put() - [addCustomBuildConfigFields](addCustomBuildConfigFields), [perVariantManifestPlaceholder](perVariantManifestPlaceholder)
* MultipleArtifact.MULTIDEX_KEEP_PROGUARD - [getMultipleArtifact](getMultipleArtifact)
* MultipleArtifact.NATIVE_DEBUG_METADATA - [appendToMultipleArtifact](appendToMultipleArtifact), [addMultipleArtifact](addMultipleArtifact)
* OutOperationRequest.toAppendTo() - [appendToMultipleArtifact](appendToMultipleArtifact)
* OutOperationRequest.toCreate() - [createSingleArtifact](createSingleArtifact)
* Plugin<Settings> - [allProjectsApkAction](allProjectsApkAction)
* ResolutionStrategy.dependencySubstitution() - [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* ScopedArtifact.CLASSES - [getScopedArtifacts](getScopedArtifacts), [asmTransformClasses](asmTransformClasses), [transformAllClasses](transformAllClasses)
* ScopedArtifacts.Scope.ALL - [getScopedArtifacts](getScopedArtifacts)
* ScopedArtifacts.Scope.PROJECT - [getScopedArtifacts](getScopedArtifacts), [asmTransformClasses](asmTransformClasses), [transformAllClasses](transformAllClasses)
* ScopedArtifacts.use() - [getScopedArtifacts](getScopedArtifacts), [asmTransformClasses](asmTransformClasses), [transformAllClasses](transformAllClasses)
* ScopedArtifactsOperation.toGet() - [getScopedArtifacts](getScopedArtifacts), [asmTransformClasses](asmTransformClasses)
* ScopedArtifactsOperation.toTransform() - [transformAllClasses](transformAllClasses)
* SingleArtifact.APK - [workerEnabledTransformation](workerEnabledTransformation), [allProjectsApkAction](allProjectsApkAction)
* SingleArtifact.ASSETS - [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset), [transformDirectory](transformDirectory)
* SingleArtifact.BUNDLE - [appendToMultipleArtifact](appendToMultipleArtifact), [addMultipleArtifact](addMultipleArtifact), [getSingleArtifact](getSingleArtifact)
* SingleArtifact.MERGED_MANIFEST - [variantOutput](variantOutput), [perVariantManifestPlaceholder](perVariantManifestPlaceholder), [createSingleArtifact](createSingleArtifact), [transformManifest](transformManifest)
* SourceDirectories.addGeneratedSourceDirectory() - [addCustomSourceFolders](addCustomSourceFolders), [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset)
* SourceDirectories.addStaticSourceDirectory() - [addCustomSourceFolders](addCustomSourceFolders)
* TaskBasedOperation.wiredWith() - [createSingleArtifact](createSingleArtifact)
* TaskBasedOperation.wiredWithDirectories() - [workerEnabledTransformation](workerEnabledTransformation), [transformDirectory](transformDirectory)
* TaskBasedOperation.wiredWithFiles() - [transformManifest](transformManifest)
* TaskOutputs.upToDateWhen() - [transformManifest](transformManifest)
* TaskProvider.flatMap() - [createSingleArtifact](createSingleArtifact)
* TaskProvider.map() - [addCustomBuildConfigFields](addCustomBuildConfigFields)
* Variant.buildConfigFields - [addCustomBuildConfigFields](addCustomBuildConfigFields)
* Variant.components - [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* Variant.manifestPlaceholders - [perVariantManifestPlaceholder](perVariantManifestPlaceholder)
* Variant.nestedComponents - [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* VariantBuilder.minSdk - [selectVariants](selectVariants)
* VariantExtensionConfig - [extendingAgp](extendingAgp)
* VariantOutputConfiguration.OutputType.SINGLE - [variantOutput](variantOutput)
* VariantOutputConfiguration.outputType - [variantOutput](variantOutput)
* VariantSelector.all() - [variantOutput](variantOutput), [selectVariants](selectVariants)
* VariantSelector.withBuildType() - [variantDependencySubstitutionTest](variantDependencySubstitutionTest), [allProjectsApkAction](allProjectsApkAction), [selectVariants](selectVariants)
* VariantSelector.withFlavor() - [variantOutput](variantOutput), [selectVariants](selectVariants)
* VariantSelector.withName() - [selectVariants](selectVariants)
* task.getOutputs() - [transformManifest](transformManifest)
## Call chains
* DslExtension.Builder().extendProjectWith().extendBuildTypeWith().extendProductFlavorWith().build() - [extendingAgp](extendingAgp)
* androidComponents.beforeVariants {} - [selectVariants](selectVariants)
* androidComponents.finalizeDsl {} - [addBuildTypeUsingDslFinalize](addBuildTypeUsingDslFinalize)
* androidComponents.onVariants {} - [addCustomSourceFolders](addCustomSourceFolders), [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset), [addCustomBuildConfigFields](addCustomBuildConfigFields), [workerEnabledTransformation](workerEnabledTransformation), [variantOutput](variantOutput), [perVariantManifestPlaceholder](perVariantManifestPlaceholder), [onVariants](onVariants), [extendingAgp](extendingAgp), [variantDependencySubstitutionTest](variantDependencySubstitutionTest), [createSingleArtifact](createSingleArtifact), [transformDirectory](transformDirectory), [getScopedArtifacts](getScopedArtifacts), [asmTransformClasses](asmTransformClasses), [appendToMultipleArtifact](appendToMultipleArtifact), [allProjectsApkAction](allProjectsApkAction), [getMultipleArtifact](getMultipleArtifact), [addMultipleArtifact](addMultipleArtifact), [getSingleArtifact](getSingleArtifact), [transformAllClasses](transformAllClasses), [transformManifest](transformManifest)
* androidComponents.registerExtension() - [extendingAgp](extendingAgp)
* androidComponents.selector().all() - [variantOutput](variantOutput), [selectVariants](selectVariants)
* androidComponents.selector().withBuildType() - [variantDependencySubstitutionTest](variantDependencySubstitutionTest), [allProjectsApkAction](allProjectsApkAction), [selectVariants](selectVariants)
* androidComponents.selector().withFlavor() - [variantOutput](variantOutput), [selectVariants](selectVariants)
* androidComponents.selector().withName() - [selectVariants](selectVariants)
* configuration.resolutionStrategy.dependencySubstitution {} - [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* settings.gradle.beforeProject {} - [allProjectsApkAction](allProjectsApkAction)
* substitute().using() - [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* task.outputs.upToDateWhen {} - [transformManifest](transformManifest)
* transformationRequest.submit() - [workerEnabledTransformation](workerEnabledTransformation)
* variant.applicationId - [onVariants](onVariants)
* variant.artifacts.add() - [addMultipleArtifact](addMultipleArtifact)
* variant.artifacts.forScope().use().toGet() - [getScopedArtifacts](getScopedArtifacts), [asmTransformClasses](asmTransformClasses)
* variant.artifacts.forScope().use().toTransform() - [transformAllClasses](transformAllClasses)
* variant.artifacts.get() - [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset), [variantOutput](variantOutput), [perVariantManifestPlaceholder](perVariantManifestPlaceholder), [transformDirectory](transformDirectory), [asmTransformClasses](asmTransformClasses), [appendToMultipleArtifact](appendToMultipleArtifact), [allProjectsApkAction](allProjectsApkAction), [addMultipleArtifact](addMultipleArtifact), [getSingleArtifact](getSingleArtifact), [transformManifest](transformManifest)
* variant.artifacts.getAll() - [getMultipleArtifact](getMultipleArtifact)
* variant.artifacts.use().wiredWith().toAppendTo() - [appendToMultipleArtifact](appendToMultipleArtifact)
* variant.artifacts.use().wiredWith().toCreate() - [createSingleArtifact](createSingleArtifact)
* variant.artifacts.use().wiredWithDirectories().toTransform() - [transformDirectory](transformDirectory)
* variant.artifacts.use().wiredWithDirectories().toTransformMany() - [workerEnabledTransformation](workerEnabledTransformation)
* variant.artifacts.use().wiredWithFiles().toTransform() - [transformManifest](transformManifest)
* variant.buildConfigFields.put() - [addCustomBuildConfigFields](addCustomBuildConfigFields)
* variant.instrumentation.transformClassesWith() - [asmTransformClasses](asmTransformClasses)
* variant.manifestPlaceholders.put() - [perVariantManifestPlaceholder](perVariantManifestPlaceholder)
* variant.sources.*.addGeneratedSourceDirectory() - [addCustomSourceFolders](addCustomSourceFolders), [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset)
* variant.sources.*.addStaticSourceDirectory() - [addCustomSourceFolders](addCustomSourceFolders)
## Others
* All projects - [allProjectsApkAction](allProjectsApkAction)
* Extending AGP DSL - [extendingAgp](extendingAgp)
* Legacy API bridging - [legacyTaskBridging](legacyTaskBridging)
* Placeholders - [perVariantManifestPlaceholder](perVariantManifestPlaceholder)
* SourceDirectories.add - [addCustomSourceFolders](addCustomSourceFolders)
* registerSourceType - [addCustomSourceFolders](addCustomSourceFolders)
# License
```
Copyright 2022 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
