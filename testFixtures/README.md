# TestFixture in android projects

This recipe shows how to use test fixtures with Android projects.

---

**Be Aware:** we don't support writing test-fixtures in Kotlin for AGP versions up to 8.4, only Java

---
Recipe has the following module structure:

| Module     | Content                                                                            |
|------------|------------------------------------------------------------------------------------|
| [lib](lib) | An Android library module has `UserRepository` and fixture.                        |
| [app](app) | An application that uses repository. It needs `lib` fixture to test code properly. |

Fixtures switched on with `testFixtures.enable = true` in a library [build](lib/build.gradle.kts) file.

Application and library module dependencies look like following:
```
app -> lib
app/test -> lib/testFixtures
```
Test dependency must be declared in Gradle script to target the fixture artifact provided by the library:
 `testImplementation testFixtures(project(":lib"))`.

You can check details of logic we test in [ViewModel](app/src/main/kotlin/ViewModel.kt),
test itself in [ViewModelTest](app/src/test/kotlin/ViewModelTest.kt) and 
fixture [UserRepoFixture](lib/src/testFixtures/java/UserRepoFixture.java).

## To Run
To execute example and run tests you need to enter command:

`./gradlew app:testDebug`

This way [app/.../ViewModelTest.kt](app/src/test/kotlin/ViewModelTest.kt) will be
executed to check if `ViewModel` works using fixture.

