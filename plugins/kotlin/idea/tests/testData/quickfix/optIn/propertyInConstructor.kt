// "Propagate 'PropertyTypeMarker' opt-in requirement to constructor" "true"

@RequiresOptIn
annotation class PropertyTypeMarker

@PropertyTypeMarker
class PropertyTypeMarked

class PropertyTypeContainer(val subject: Property<caret>TypeMarked)
