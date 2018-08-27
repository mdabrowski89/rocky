package pl.mobite.rocky.ui.map

import io.reactivex.functions.BiFunction

class MapReducer: BiFunction<MapViewState, MapResult, MapViewState> {

    override fun apply(prevState: MapViewState, result: MapResult): MapViewState {
        return when(result) {
            is MapResult.MapReadyResult -> prevState.copy(reRenderFlag = !prevState.reRenderFlag)
            is MapResult.LoadPlacesResult ->
                when(result) {
                    is MapResult.LoadPlacesResult.InFlight ->
                        prevState.copy(isLoading = true, error = null)
                    is MapResult.LoadPlacesResult.Success ->
                        prevState.copy(isLoading = false, places = result.places, placesTimestamp = System.currentTimeMillis())
                    is MapResult.LoadPlacesResult.Failure ->
                        prevState.copy(isLoading = false, error = result.throwable)
                }
            is MapResult.AllPlacesGoneResult -> prevState.copy(places = emptyList(), placesTimestamp = null)
            is MapResult.ErrorDisplayedResult -> prevState.copy(error = null)
        }
    }
}