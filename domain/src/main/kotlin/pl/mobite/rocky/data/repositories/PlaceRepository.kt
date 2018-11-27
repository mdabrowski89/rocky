package pl.mobite.rocky.data.repositories

import pl.mobite.rocky.data.repositories.models.Place


interface PlaceRepository {

    @Throws(Throwable::class)
    fun getPlacesFrom1990(query: String): List<Place>
}