package pl.mobite.rocky.data.repositories.place.mappers

import io.reactivex.functions.Function
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.models.PlaceCords
import pl.mobite.rocky.data.remote.models.PlaceApi


class PlaceApiToPlaceMapper: Function<PlaceApi, Place> {

    override fun apply(placeApi: PlaceApi): Place? {
        val name = placeApi.name
        val openYear = placeApi.lifeSpan?.begin?.toIntOrNull()
        val lat = placeApi.coordinates?.latitude?.toDoubleOrNull()
        val lng = placeApi.coordinates?.longitude?.toDoubleOrNull()

        return if (name != null && openYear != null && lat != null && lng != null) {
            Place(name, openYear, PlaceCords(lat, lng))
        } else {
            null
        }
    }
}