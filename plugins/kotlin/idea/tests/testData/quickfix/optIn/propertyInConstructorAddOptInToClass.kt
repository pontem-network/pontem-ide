// "Opt in for 'PropertyTypeMarker' on containing class 'PropertyTypeContainer'" "true"

@RequiresOptIn
annotation class PropertyTypeMarker

@PropertyTypeMarker
class PropertyTypeMarked

class PropertyTypeContainer(val subject: Property<caret>TypeMarked)
