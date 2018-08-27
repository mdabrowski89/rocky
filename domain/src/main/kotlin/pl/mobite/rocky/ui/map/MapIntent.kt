package pl.mobite.rocky.ui.map


sealed class MapIntent {

    object MapReadyIntent: MapIntent()

    data class SearchPlacesIntent(val query: String): MapIntent()

    object AllPlacesGoneIntent: MapIntent()

    object ErrorDisplayedIntent: MapIntent()
}