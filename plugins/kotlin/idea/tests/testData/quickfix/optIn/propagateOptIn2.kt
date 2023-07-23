// "Propagate 'UnstableApi' opt-in requirement to containing class 'Derived'" "true"

@RequiresOptIn
annotation class UnstableApi

@SubclassOptInRequired(UnstableApi::class)
interface Base {
    @UnstableApi
    fun foo()
}

abstract class Derived : Base {
    override fun foo<caret>(){}
}