package pl.mobite.rocky.data.repositories.place.mappers

import io.reactivex.functions.Function
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.models.PlaceCords
import pl.mobite.rocky.data.remote.models.PlaceApi


class PlaceApiToPlaceListMapper: Function<List<PlaceApi>, List<Place>> {

    override fun apply(placeApiList: List<PlaceApi>): List<Place> {
        return placeApiList.mapNotNull { placeApi ->
            val name = placeApi.name
            val openYear = placeApi.lifeSpan?.begin?.toIntOrNull()
            val lat = placeApi.coordinates?.latitude?.toDoubleOrNull()
            val lng = placeApi.coordinates?.longitude?.toDoubleOrNull()

            if (name != null && openYear != null && lat != null && lng != null) {
                Place(name, openYear, PlaceCords(lat, lng))
            } else {
                null
            }
        }
    }
}