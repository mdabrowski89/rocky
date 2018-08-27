package pl.mobite.rocky.ui.map


sealed class MapAction {

    object MapReadyAction: MapAction()

    data class LoadPlacesAction(val query: String): MapAction()

    object AllPlacesGoneAction: MapAction()

    object ErrorDisplayedAction: MapAction()
}