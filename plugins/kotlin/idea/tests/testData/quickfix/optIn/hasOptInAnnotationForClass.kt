// "Opt in for 'B' on containing class 'C'" "true"
@RequiresOptIn
annotation class A

@RequiresOptIn
annotation class B

@A
fun f1() = Unit

@B
fun f2() = Unit

@OptIn(A::class)
class C {
    fun someFun() {
        f1()
        <caret>f2()
    }
}