package pl.mobite.rocky.ui.components.map


sealed class MapIntent {

    object MapReadyIntent: MapIntent()

    data class SearchPlacesIntent(val query: String): MapIntent()

    object AllMarkersGoneIntent: MapIntent()
}