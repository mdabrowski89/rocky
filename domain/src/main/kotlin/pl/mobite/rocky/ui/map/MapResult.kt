package pl.mobite.rocky.ui.map

import pl.mobite.rocky.data.models.Place


sealed class MapResult {

    object ReRenderResult: MapResult()

    sealed class LoadPlacesResult: MapResult() {

        object InFlight: LoadPlacesResult()

        data class Failure(val throwable: Throwable): LoadPlacesResult()

        data class Success(val places: List<Place>, val timestamp: Long): LoadPlacesResult()
    }

    object ClearSearchResultsResult: MapResult()

    object ClearErrorResult: MapResult()
}
