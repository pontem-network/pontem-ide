fun foo(f: (String?) -> Int) {}

fun test() {
    foo {
        if (it != null) return@foo 1
        <caret>(1+1)
        0
    }
}