// "Propagate 'MyExperimentalAPI' opt-in requirement to 'outer'" "true"

@RequiresOptIn
annotation class MyExperimentalAPI

@MyExperimentalAPI
fun foo() {}

fun outer() {
    fun bar() {
        foo<caret>()
    }
}
