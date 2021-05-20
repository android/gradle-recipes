Building up on the 'customizeAgpDsl' recipe, this recipe will also add an extension
to the Android Gradle Plugin [Variant] interfaces. This is particularly useful when
such a plugin would like to offer a Variant scoped object that can be looked up
by other third party plugins.

In this example, the BuildTypeExtension type is a DSL interface declaration which
is attached to the Android Gradle Plugin build-type dsl element using "exampleDsl"
namespace. The DSL extension is then used in the beforeVariants API to create a
variant scoped object and register it.

A second plugin called ConsumerPlugin (also applied on the same project) will look
up the Variant scoped object to configure the ExampleTask. This demonstrate how
two plugins can share variant scoped objects without making explicit direct
connections.

Because [VariantExtension.parameters] is declared as property, you could extend the
example further by having a Task providing the value.