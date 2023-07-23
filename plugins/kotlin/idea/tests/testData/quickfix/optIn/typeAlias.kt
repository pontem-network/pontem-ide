// "Opt in for 'AliasMarker' on 'AliasMarkerUsage'" "true"


@RequiresOptIn
annotation class AliasMarker

@AliasMarker
class AliasTarget

typealias AliasMarkerUsage = <caret>AliasTarget
