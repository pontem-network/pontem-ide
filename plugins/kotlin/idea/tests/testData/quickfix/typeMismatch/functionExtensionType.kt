// "Change return type of enclosing function 'myFunction' to '(Int) -> Boolean'" "true"
// WITH_STDLIB

fun foo() {
    fun myFunction(s: String): (String, Int) -> Boolean = <caret>s::verifyData
}

fun String.verifyData(a: Int) = this.length > a
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.ChangeCallableReturnTypeFix$ForEnclosing