// "Propagate 'SubclassOptInRequired(A::class)' opt-in requirement to 'SomeImplementation'" "true"
// ERROR: This declaration needs opt-in. Its usage must be marked with '@B' or '@OptIn(B::class)'

@RequiresOptIn
annotation class A

@RequiresOptIn
annotation class B

@SubclassOptInRequired(A::class)
interface LibraryA

@SubclassOptInRequired(B::class)
interface LibraryB

interface SomeImplementation : LibraryA<caret>, LibraryB