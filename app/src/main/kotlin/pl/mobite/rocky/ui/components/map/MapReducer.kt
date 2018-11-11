package pl.mobite.rocky.ui.components.map

import android.support.annotation.VisibleForTesting
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.functions.BiFunction
import pl.mobite.rocky.ui.models.MarkerData
import pl.mobite.rocky.ui.models.ViewStateError
import pl.mobite.rocky.data.repositories.models.Place

class MapReducer: BiFunction<MapViewState, MapResult, MapViewState> {

    override fun apply(prevState: MapViewState, result: MapResult): MapViewState {
        return when(result) {
            is MapResult.ReRenderResult -> prevState.reRender()
            is MapResult.LoadPlacesResult ->
                when(result) {
                    is MapResult.LoadPlacesResult.InFlight ->
                        prevState.loading()
                    is MapResult.LoadPlacesResult.Success ->
                        prevState.withData(result.places.toMarkerDataList(), result.timestamp)
                    is MapResult.LoadPlacesResult.Failure ->
                        prevState.withError(result.throwable)
                }
            is MapResult.ClearSearchResultsResult -> prevState.clearData()
        }
    }
}

fun MapViewState.reRender() = this.copy(
        reRenderFlag = !this.reRenderFlag
)

fun MapViewState.loading() = this.copy(
        isLoading = true,
        error = null
)

fun MapViewState.withData(markerDataList: List<MarkerData>, dataCreationTimestamp: Long) = this.copy(
        isLoading = false,
        error = null,
        markerDataList = markerDataList,
        dataCreationTimestamp = dataCreationTimestamp
)

fun MapViewState.withError(throwable: Throwable) = this.copy(
        isLoading = false,
        error = ViewStateError(throwable)
)

fun MapViewState.clearData() = this.copy(
        markerDataList = emptyList(),
        dataCreationTimestamp = null
)

@VisibleForTesting
fun List<Place>.toMarkerDataList() = this.map { place ->
    val latLng = LatLng(place.lat, place.lng)
    val description = "${place.name}, ${place.openYear}"
    val timeToLive = (place.openYear - 1990) * 1000L
    MarkerData(MarkerOptions().position(latLng).title(place.name), description, timeToLive)
}