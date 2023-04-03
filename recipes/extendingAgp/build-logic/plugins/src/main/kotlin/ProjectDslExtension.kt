/**
 * This is the extension type for extending [com.android.build.api.dsl.CommonExtension].
 *
 * There will be single instance of this type instantiated for the `android` extension.
 *
 * This extension type is registered by calling
 * [com.android.build.api.variant.AndroidComponents.registerExtension] method.
 *
 */
interface ProjectDslExtension {
    var settingOne: String
    var settingTwo: Int
}
