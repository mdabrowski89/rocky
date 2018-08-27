package pl.mobite.rocky.ui.map

import pl.mobite.rocky.data.models.Place


data class MapViewState(
        val reRenderFlag: Boolean,
        val isLoading: Boolean,
        val places: List<Place>,
        val placesTimestamp: Long?,
        val error: Throwable?
) {
    companion object Factory {
        fun default() = MapViewState(
                reRenderFlag = false,
                isLoading = false,
                places = emptyList(),
                placesTimestamp = null,
                error = null)
    }
}
