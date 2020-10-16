import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class CustomPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withType(AppPlugin::class.java) {
            val extension = project.extensions.getByName("android") as ApplicationExtension<*, *, *, *, *>

            extension.onVariants {
                // disable all unit tests for apps (only using instrumentation tests)
                unitTest {
                    enabled = false
                }
            }
        }

        project.plugins.withType(LibraryPlugin::class.java) {
            val extension = project.extensions.getByName("android") as LibraryExtension<*, *, *, *, *>

            extension.onVariants.withBuildType("debug") {
                // Disable instrumentation for debug
                androidTest {
                    enabled = false
                } 
            }

            extension.onVariants.withBuildType("release") {
                // Disable unit test for release
                unitTest {
                    enabled = false
                }
            }
        }
    }
}