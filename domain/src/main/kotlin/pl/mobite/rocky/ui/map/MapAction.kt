package pl.mobite.rocky.ui.map


sealed class MapAction {

    object ReRenderAction: MapAction()

    data class LoadPlacesAction(val query: String): MapAction()

    object ClearSearchResultsAction: MapAction()
}