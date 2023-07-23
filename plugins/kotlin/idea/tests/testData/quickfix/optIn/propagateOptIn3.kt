// "Propagate 'UnstableApi' opt-in requirement to 'SomeImplementation'" "true"

@RequiresOptIn
annotation class UnstableApi

@SubclassOptInRequired(UnstableApi::class)
interface CoreLibraryApi

class SomeImplementation : CoreLibraryApi<caret>