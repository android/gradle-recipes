# Use the Instrumentation.transformClassesWith() API to transform classes with ASM

This sample shows how to use the [`Instrumentation.transformClassesWith()`](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/variant/Instrumentation#transformClassesWith(java.lang.Class,com.android.build.api.instrumentation.InstrumentationScope,kotlin.Function1)) API to transform classes using an ASM
class visitor.

This recipe contains the following directories:

| Module                     | Content                                                     |
|----------------------------|-------------------------------------------------------------|
| [build-logic](build-logic) | Contains the Project plugin that is the core of the recipe. |
| [app](app)                 | An Android application that has the plugin applied.         |

The [build-logic](build-logic) sub-project contains the [`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt), [`ExampleClassVisitorFactory`](build-logic/plugins/src/main/kotlin/ExampleClassVisitorFactory.kt),
[`ClassMethodVisitor`](build-logic/plugins/src/main/kotlin/ClassMethodVisitor.kt) and [`CheckAsmTransformationTask`](build-logic/plugins/src/main/kotlin/CheckAsmTransformationTask.kt)
classes.

[`CustomPlugin`](build-logic/plugins/src/main/kotlin/CustomPlugin.kt) contains the code which calls the `transformClassesWith()` API on the classes. In this method,
the class visitor which performs the transformation is specified, as well as the instrumentation scope and the
instrumentation parameter initialization. The [`ExampleClassVisitorFactory`](build-logic/plugins/src/main/kotlin/ExampleClassVisitorFactory.kt) is used as the class visitor factory,
which transforms the classes using the [`ClassMethodVisitor`](build-logic/plugins/src/main/kotlin/ClassMethodVisitor.kt).
It contains `ExampleParams` as well, which are the parameters specified in the API call and used in the factory. An
example usage is:

```
variant.instrumentation.transformClassesWith(
    ExampleClassVisitorFactory::class.java,
    InstrumentationScope.PROJECT
) { params ->
    params.newMethodName.set("transformedMethod")
}
```

Here, the `newMethodName` property is a parameter specified in `ExampleParams`. Lastly, the
[`CheckAsmTransformationTask`](build-logic/plugins/src/main/kotlin/CheckAsmTransformationTask.kt) is included in this sample, which validates the transformation. Because this task
creates a dependency on the classes, calling it runs the necessary tasks to gather the classes artifact, transform it,
and validate the transformation.

## To Run
To execute example you need to enter command:

`./gradlew :app:checkDebugAsmTransformation`
