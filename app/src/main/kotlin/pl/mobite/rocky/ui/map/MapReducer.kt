package pl.mobite.rocky.ui.map

import android.support.annotation.VisibleForTesting
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.functions.BiFunction
import pl.mobite.rocky.data.model.MarkerData
import pl.mobite.rocky.data.models.Place

class MapReducer: BiFunction<MapViewState, MapResult, MapViewState> {

    override fun apply(prevState: MapViewState, result: MapResult): MapViewState {
        return when(result) {
            is MapResult.ReRenderResult -> prevState.copy(reRenderFlag = !prevState.reRenderFlag)
            is MapResult.LoadPlacesResult ->
                when(result) {
                    is MapResult.LoadPlacesResult.InFlight ->
                        prevState.copy(isLoading = true, error = null)
                    is MapResult.LoadPlacesResult.Success ->
                        prevState.copy(isLoading = false, error = null, markerDataList = result.places.toMarkerDataList(), dataCreationTimestamp = result.timestamp)
                    is MapResult.LoadPlacesResult.Failure ->
                        prevState.copy(isLoading = false, error = result.throwable)
                }
            is MapResult.ClearSearchResultsResult -> prevState.copy(markerDataList = emptyList(), dataCreationTimestamp = null)
            is MapResult.ClearErrorResult -> prevState.copy(error = null)
        }
    }
}

@VisibleForTesting
fun List<Place>.toMarkerDataList() = this.map { place ->
    val latLng = LatLng(place.lat, place.lng)
    val description = "${place.name}, ${place.openYear}"
    val timeToLive = (place.openYear - 1990) * 1000L
    MarkerData(MarkerOptions().position(latLng).title(place.name), description, timeToLive)
}