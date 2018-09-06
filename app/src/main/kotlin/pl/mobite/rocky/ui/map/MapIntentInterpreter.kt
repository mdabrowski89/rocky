package pl.mobite.rocky.ui.map

import io.reactivex.functions.Function


class MapIntentInterpreter: Function<MapIntent, MapAction> {

    override fun apply(intent: MapIntent): MapAction {
        return when(intent) {
            is MapIntent.MapReadyIntent -> MapAction.ReRenderAction
            is MapIntent.SearchPlacesIntent -> MapAction.LoadPlacesAction(intent.query)
            is MapIntent.AllMarkersGoneIntent -> MapAction.ClearSearchResultsAction
        }
    }
}