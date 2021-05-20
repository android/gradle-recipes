This recipe shows how a third party plugin can add DSL elements basically anywhere
in the android DSL tree. This is particularly useful when such a plugin would like
to configure behaviors specific to a build type or a product flavor by having such
configuration directly attached to the buildTypes or Flavor declaration in the
android dsl block.

In this example, the BuildTypeExtension type is a DSL interface declaration which
is attached to the Android Gradle Plugin build-type dsl element using "exampleDsl"
namespace.

Any DSL element definition that extend ExtensionAware can have third party
extensions attached to it, see Android Gradle Plugin DSL javadocs.

See full documentation on Gradle's web site at :
https://docs.gradle.org/current/userguide/custom_plugins.html#sec:getting_input_from_the_build

In groovy, the end-users will be able to use the extension as follow :
android {
    buildTypes {
        debug {
            exampleDsl {
                invocationParameters = "-debug -log"
            }
        }
    }
}

while in Kotlin, because the script are compiled, you need to use the following
syntax :
android {
    buildTypes {
        debug {
            the<BuildTypeExtension>().invocationParameters = "-debug -log"
        }
    }
}

To be able to use the extension DSL element to configure tasks, the easiest is to
hook up the Task creation in the onVariants API callback and look up the element
from the android block build-type element.