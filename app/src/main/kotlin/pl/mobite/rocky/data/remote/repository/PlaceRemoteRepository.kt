package pl.mobite.rocky.data.remote.repository

import pl.mobite.rocky.data.repositories.models.Place


interface PlaceRemoteRepository {

    @Throws(Throwable::class)
    fun fetchAllPlacesFrom1990(query: String): List<Place>
}