package pl.mobite.rocky.ui.map

import pl.mobite.rocky.data.models.Place


sealed class MapResult {

    object MapReadyResult: MapResult()

    sealed class LoadPlacesResult: MapResult() {

        object InFlight: LoadPlacesResult()

        data class Failure(val throwable: Throwable): LoadPlacesResult()

        data class Success(val places: List<Place>): LoadPlacesResult()
    }

    object AllPlacesGoneResult: MapResult()

    object ErrorDisplayedResult: MapResult()
}
