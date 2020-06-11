import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class ManifestProducerTask: DefaultTask() {
    @get:InputFile
    abstract val gitInfoFile: RegularFileProperty

    @get:OutputFile
    abstract val outputManifest: RegularFileProperty

    @TaskAction
    fun taskAction() {

        val gitVersion = gitInfoFile.get().asFile.readText()
        val manifest = """<?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="com.android.build.example.minimal"
        android:versionName="${gitVersion}"
        android:versionCode="1" >
        <application android:label="Minimal">
            <activity android:name="MainActivity">
                <intent-filter>
                    <action android:name="android.intent.action.MAIN" />
                    <category android:name="android.intent.category.LAUNCHER" />
                </intent-filter>
            </activity>
        </application>
    </manifest>
        """
        println("Writes to " + outputManifest.get().asFile.absolutePath)
        outputManifest.get().asFile.writeText(manifest)
    }
}
