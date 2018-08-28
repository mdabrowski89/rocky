package pl.mobite.rocky.data.repositories.place.filters

import io.reactivex.functions.Predicate
import pl.mobite.rocky.data.remote.models.PlaceApi


class PlaceApiByOpeningYearFilter(private val openingYear: Int) : Predicate<PlaceApi> {

    override fun test(placeApi: PlaceApi): Boolean {
        return placeApi.lifeSpan?.begin?.toIntOrNull()?.let {
            openYear -> openYear >= openingYear } ?: false
    }
}