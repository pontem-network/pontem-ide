// "Propagate 'TopMarker' opt-in requirement to 'topUserVal'" "true"

@RequiresOptIn
annotation class TopMarker

@TopMarker
class TopClass

@Target(AnnotationTarget.TYPE)
@TopMarker
annotation class TopAnn

val topUserVal: @<caret>TopAnn TopClass? = null
