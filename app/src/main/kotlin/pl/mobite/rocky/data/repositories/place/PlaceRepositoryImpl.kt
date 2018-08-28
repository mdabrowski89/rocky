package pl.mobite.rocky.data.repositories.place

import io.reactivex.Single
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.remote.services.PlaceApiService
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.data.repositories.place.mappers.PlaceApiToPlaceListMapper


class PlaceRepositoryImpl: PlaceRepository {

    private val placesApiService by lazy { PlaceApiService() }

    override fun getPlacesFrom1990(query: String): Single<List<Place>> {
        val placeListMapper = PlaceApiToPlaceListMapper()
        return placesApiService
                .fetchAllPlacesFrom1990(query)
                .map(placeListMapper)
    }
}