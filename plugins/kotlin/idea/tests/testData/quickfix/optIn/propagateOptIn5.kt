// "Propagate 'UnstableApi' opt-in requirement to 'SomeImplementation'" "true"

@RequiresOptIn
annotation class UnstableApi

@SubclassOptInRequired(UnstableApi::class)
interface CoreLibraryApi

enum class SomeImplementation : CoreLibraryApi<caret>