// "Opt in for 'MyExperimentalAPI' on containing class 'Bar'" "true"
// PRIORITY: HIGH

package a.b

@RequiresOptIn
@Target(AnnotationTarget.FUNCTION)
annotation class MyExperimentalAPI

@MyExperimentalAPI
fun foo() {}

class Bar {
    fun bar() {
        foo<caret>()
    }
}
