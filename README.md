# Recipes for AGP version `8.2.2`
This branch contains recipes compatible with AGP 8.2.2. If you want to find recipes
for other AGP versions, switch to the corresponding `agp-*` branch.

This branch is read only. Contributions are only accepted on the `studio-main` branch. See `CONTRIBUTION.md`
there.
# Recipes Index
Index is organized in categories, offering different ways to reach the recipe you want.
## Themes
* Android Assets - [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset)
* Android Manifest - [perVariantManifestPlaceholder](perVariantManifestPlaceholder), [transformManifest](transformManifest), [createSingleArtifact](createSingleArtifact)
* Artifact API - [getSingleArtifact](getSingleArtifact), [transformAllClasses](transformAllClasses), [workerEnabledTransformation](workerEnabledTransformation), [appendToMultipleArtifact](appendToMultipleArtifact), [getMultipleArtifact](getMultipleArtifact), [createSingleArtifact](createSingleArtifact), [addMultipleArtifact](addMultipleArtifact), [getScopedArtifacts](getScopedArtifacts)
* DSL - [extendingAgp](extendingAgp), [addBuildTypeUsingDslFinalize](addBuildTypeUsingDslFinalize)
* Dependency Resolution - [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* Sources - [addCustomSourceFolders](addCustomSourceFolders), [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset)
## APIs
* AndroidComponentsExtension.beforeVariants() - [selectVariants](selectVariants)
* AndroidComponentsExtension.onVariants() - [allProjectsApkAction](allProjectsApkAction), [perVariantManifestPlaceholder](perVariantManifestPlaceholder), [getSingleArtifact](getSingleArtifact), [extendingAgp](extendingAgp), [addCustomBuildConfigFields](addCustomBuildConfigFields), [transformAllClasses](transformAllClasses), [addCustomSourceFolders](addCustomSourceFolders), [transformManifest](transformManifest), [workerEnabledTransformation](workerEnabledTransformation), [onVariants](onVariants), [legacyTaskBridging](legacyTaskBridging), [appendToMultipleArtifact](appendToMultipleArtifact), [addCustomAsset](addCustomAsset), [variantDependencySubstitutionTest](variantDependencySubstitutionTest), [getMultipleArtifact](getMultipleArtifact), [createSingleArtifact](createSingleArtifact), [addMultipleArtifact](addMultipleArtifact), [getScopedArtifacts](getScopedArtifacts)
* AndroidComponentsExtension.registerExtension() - [extendingAgp](extendingAgp)
* AndroidComponentsExtension.selector() - [allProjectsApkAction](allProjectsApkAction), [selectVariants](selectVariants)
* ApplicationVariant.applicationId - [onVariants](onVariants)
* ArtifactTransformationRequest - [workerEnabledTransformation](workerEnabledTransformation)
* Artifacts.add() - [addMultipleArtifact](addMultipleArtifact)
* Artifacts.forScope() - [transformAllClasses](transformAllClasses), [getScopedArtifacts](getScopedArtifacts)
* Artifacts.get() - [allProjectsApkAction](allProjectsApkAction), [perVariantManifestPlaceholder](perVariantManifestPlaceholder), [getSingleArtifact](getSingleArtifact), [legacyTaskBridging](legacyTaskBridging), [appendToMultipleArtifact](appendToMultipleArtifact), [addCustomAsset](addCustomAsset), [addMultipleArtifact](addMultipleArtifact)
* Artifacts.getAll() - [getMultipleArtifact](getMultipleArtifact)
* Artifacts.use() - [transformManifest](transformManifest), [workerEnabledTransformation](workerEnabledTransformation), [appendToMultipleArtifact](appendToMultipleArtifact), [createSingleArtifact](createSingleArtifact)
* BuildConfigField() - [addCustomBuildConfigFields](addCustomBuildConfigFields)
* BuiltArtifact - [workerEnabledTransformation](workerEnabledTransformation)
* CanMinifyAndroidResourcesBuilder.shrinkResources - [selectVariants](selectVariants)
* CanMinifyCodeBuilder.isMinifyEnabled - [selectVariants](selectVariants)
* Component.artifacts - [transformManifest](transformManifest), [legacyTaskBridging](legacyTaskBridging), [appendToMultipleArtifact](appendToMultipleArtifact), [addCustomAsset](addCustomAsset), [getMultipleArtifact](getMultipleArtifact), [createSingleArtifact](createSingleArtifact), [addMultipleArtifact](addMultipleArtifact), [getScopedArtifacts](getScopedArtifacts)
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
* InAndOutDirectoryOperationRequest.toTransformMany() - [workerEnabledTransformation](workerEnabledTransformation)
* InAndOutFileOperationRequest.toTransform() - [transformManifest](transformManifest)
* MapProperty.put() - [perVariantManifestPlaceholder](perVariantManifestPlaceholder), [addCustomBuildConfigFields](addCustomBuildConfigFields)
* MultipleArtifact.MULTIDEX_KEEP_PROGUARD - [getMultipleArtifact](getMultipleArtifact)
* MultipleArtifact.NATIVE_DEBUG_METADATA - [appendToMultipleArtifact](appendToMultipleArtifact), [addMultipleArtifact](addMultipleArtifact)
* OutOperationRequest.toAppendTo() - [appendToMultipleArtifact](appendToMultipleArtifact)
* OutOperationRequest.toCreate() - [createSingleArtifact](createSingleArtifact)
* Plugin<Settings> - [allProjectsApkAction](allProjectsApkAction)
* ResolutionStrategy.dependencySubstitution() - [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* ScopedArtifact.CLASSES - [transformAllClasses](transformAllClasses), [getScopedArtifacts](getScopedArtifacts)
* ScopedArtifacts.Scope.ALL - [getScopedArtifacts](getScopedArtifacts)
* ScopedArtifacts.Scope.PROJECT - [transformAllClasses](transformAllClasses), [getScopedArtifacts](getScopedArtifacts)
* ScopedArtifacts.use() - [transformAllClasses](transformAllClasses), [getScopedArtifacts](getScopedArtifacts)
* ScopedArtifactsOperation.toGet() - [getScopedArtifacts](getScopedArtifacts)
* ScopedArtifactsOperation.toTransform() - [transformAllClasses](transformAllClasses)
* SingleArtifact.APK - [allProjectsApkAction](allProjectsApkAction), [workerEnabledTransformation](workerEnabledTransformation)
* SingleArtifact.ASSETS - [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset)
* SingleArtifact.BUNDLE - [getSingleArtifact](getSingleArtifact), [appendToMultipleArtifact](appendToMultipleArtifact), [addMultipleArtifact](addMultipleArtifact)
* SingleArtifact.MERGED_MANIFEST - [perVariantManifestPlaceholder](perVariantManifestPlaceholder), [transformManifest](transformManifest), [createSingleArtifact](createSingleArtifact)
* SourceDirectories.addGeneratedSourceDirectory() - [addCustomSourceFolders](addCustomSourceFolders), [legacyTaskBridging](legacyTaskBridging), [addCustomAsset](addCustomAsset)
* SourceDirectories.addStaticSourceDirectory() - [addCustomSourceFolders](addCustomSourceFolders)
* TaskBasedOperation.wiredWith() - [createSingleArtifact](createSingleArtifact)
* TaskBasedOperation.wiredWithDirectories() - [workerEnabledTransformation](workerEnabledTransformation)
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
* VariantSelector.all() - [selectVariants](selectVariants)
* VariantSelector.withBuildType() - [allProjectsApkAction](allProjectsApkAction), [selectVariants](selectVariants), [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* VariantSelector.withFlavor() - [selectVariants](selectVariants)
* VariantSelector.withName() - [selectVariants](selectVariants)
* task.getOutputs() - [transformManifest](transformManifest)
## Call chains
* DslExtension.Builder().extendProjectWith().extendBuildTypeWith().extendProductFlavorWith().build() - [extendingAgp](extendingAgp)
* androidComponents.beforeVariants {} - [selectVariants](selectVariants)
* androidComponents.finalizeDsl {} - [addBuildTypeUsingDslFinalize](addBuildTypeUsingDslFinalize)
* androidComponents.onVariants {} - [allProjectsApkAction](allProjectsApkAction), [perVariantManifestPlaceholder](perVariantManifestPlaceholder), [getSingleArtifact](getSingleArtifact), [extendingAgp](extendingAgp), [addCustomBuildConfigFields](addCustomBuildConfigFields), [transformAllClasses](transformAllClasses), [addCustomSourceFolders](addCustomSourceFolders), [transformManifest](transformManifest), [workerEnabledTransformation](workerEnabledTransformation), [onVariants](onVariants), [legacyTaskBridging](legacyTaskBridging), [appendToMultipleArtifact](appendToMultipleArtifact), [addCustomAsset](addCustomAsset), [variantDependencySubstitutionTest](variantDependencySubstitutionTest), [getMultipleArtifact](getMultipleArtifact), [createSingleArtifact](createSingleArtifact), [addMultipleArtifact](addMultipleArtifact), [getScopedArtifacts](getScopedArtifacts)
* androidComponents.registerExtension() - [extendingAgp](extendingAgp)
* androidComponents.selector().all() - [selectVariants](selectVariants)
* androidComponents.selector().withBuildType() - [allProjectsApkAction](allProjectsApkAction), [selectVariants](selectVariants), [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* androidComponents.selector().withFlavor() - [selectVariants](selectVariants)
* androidComponents.selector().withName() - [selectVariants](selectVariants)
* configuration.resolutionStrategy.dependencySubstitution {} - [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* settings.gradle.beforeProject {} - [allProjectsApkAction](allProjectsApkAction)
* substitute().using() - [variantDependencySubstitutionTest](variantDependencySubstitutionTest)
* task.outputs.upToDateWhen {} - [transformManifest](transformManifest)
* transformationRequest.submit() - [workerEnabledTransformation](workerEnabledTransformation)
* variant.applicationId - [onVariants](onVariants)
* variant.artifacts.add() - [addMultipleArtifact](addMultipleArtifact)
* variant.artifacts.forScope().use().toGet() - [getScopedArtifacts](getScopedArtifacts)
* variant.artifacts.forScope().use().toTransform() - [transformAllClasses](transformAllClasses)
* variant.artifacts.get() - [allProjectsApkAction](allProjectsApkAction), [perVariantManifestPlaceholder](perVariantManifestPlaceholder), [getSingleArtifact](getSingleArtifact), [legacyTaskBridging](legacyTaskBridging), [appendToMultipleArtifact](appendToMultipleArtifact), [addCustomAsset](addCustomAsset), [addMultipleArtifact](addMultipleArtifact)
* variant.artifacts.getAll() - [getMultipleArtifact](getMultipleArtifact)
* variant.artifacts.use().wiredWith().toAppendTo() - [appendToMultipleArtifact](appendToMultipleArtifact)
* variant.artifacts.use().wiredWith().toCreate() - [createSingleArtifact](createSingleArtifact)
* variant.artifacts.use().wiredWithDirectories().toTransformMany() - [workerEnabledTransformation](workerEnabledTransformation)
* variant.artifacts.use().wiredWithFiles().toTransform() - [transformManifest](transformManifest)
* variant.buildConfigFields.put() - [addCustomBuildConfigFields](addCustomBuildConfigFields)
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
