// "Propagate 'MyExperimentalAPI' opt-in requirement to 'bar'" "true"

@RequiresOptIn
annotation class MyExperimentalAPI

@MyExperimentalAPI
fun foo() {}

class Bar {
    fun bar() {
        foo<caret>()
    }
}
