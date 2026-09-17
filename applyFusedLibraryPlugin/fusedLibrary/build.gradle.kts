/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.android.fusedlibrary)
    `maven-publish`
}

androidFusedLibrary {
    namespace = "com.example.fusedlibrary"
    minSdk {
        version = release(21)
    }

    // If aarMetadata is not explicitly specified,
    // aar metadata will be generated based on dependencies.
    aarMetadata {
        minCompileSdk = 21
        minCompileSdkExtension = 1
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "my-company"
            artifactId = "my-fused-library"
            version = "1.0"
            from(components["fusedLibraryComponent"])
        }
    }
    repositories {
        maven {
            name = "myrepo"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}

dependencies {
    include(project(":androidLib1"))
    include(project(":androidLib2"))
    include("com.google.code.gson:gson:2.10.1")
    include(files("libs/simple-jar-with-A_DoIExist-class.jar"))
}
