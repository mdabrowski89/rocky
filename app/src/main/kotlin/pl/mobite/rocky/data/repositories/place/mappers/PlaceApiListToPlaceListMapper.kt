package pl.mobite.rocky.data.repositories.place.mappers

import io.reactivex.functions.Function
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.remote.models.PlaceApi


class PlaceApiListToPlaceListMapper: Function<List<PlaceApi>, List<Place>> {

    override fun apply(placeApiList: List<PlaceApi>): List<Place> {
        val placeApiToPlaceMapper = PlaceApiToPlaceMapper()
        return placeApiList.mapNotNull(placeApiToPlaceMapper::apply)
    }
}