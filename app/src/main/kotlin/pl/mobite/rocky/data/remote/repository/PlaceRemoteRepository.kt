package pl.mobite.rocky.data.remote.repository

import io.reactivex.Single
import pl.mobite.rocky.data.repositories.models.Place


interface PlaceRemoteRepository {

    fun fetchAllPlacesFrom1990(query: String): Single<List<Place>>
}