package pl.mobite.rocky.data.repositories

import pl.mobite.rocky.data.remote.repository.PlaceRemoteRepository
import pl.mobite.rocky.data.repositories.models.Place


class PlaceRepositoryImpl(
    private val placeRemoteRepository: PlaceRemoteRepository
): PlaceRepository {

    override fun getPlacesFrom1990(query: String): List<Place> {
        return placeRemoteRepository
            .fetchAllPlacesFrom1990(query)
    }
}