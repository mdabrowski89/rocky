package pl.mobite.rocky.data.remote.repository

import pl.mobite.rocky.data.repositories.models.Place


interface PlaceRemoteRepository {

    fun fetchAllPlacesFrom1990(query: String): List<Place>
}