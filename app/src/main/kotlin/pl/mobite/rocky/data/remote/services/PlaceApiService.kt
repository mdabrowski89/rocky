package pl.mobite.rocky.data.remote.services

import io.reactivex.Single
import pl.mobite.rocky.data.remote.models.PlaceApi


interface PlaceApiService {

    fun fetchAllPlacesFrom1990(query: String): Single<List<PlaceApi>>
}