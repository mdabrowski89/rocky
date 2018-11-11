package pl.mobite.rocky.data.repositories

import io.reactivex.Single
import pl.mobite.rocky.data.repositories.models.Place


interface PlaceRepository {

    fun getPlacesFrom1990(query: String): Single<List<Place>>
}