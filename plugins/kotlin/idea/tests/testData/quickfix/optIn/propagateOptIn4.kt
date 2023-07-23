// "Propagate 'UnstableApi' opt-in requirement to 'SomeImplementation'" "true"

@RequiresOptIn
annotation class UnstableApi

@SubclassOptInRequired(UnstableApi::class)
interface CoreLibraryApi

sealed interface SomeImplementation : CoreLibraryApi<caret>