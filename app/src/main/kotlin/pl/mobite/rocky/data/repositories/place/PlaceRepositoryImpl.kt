package pl.mobite.rocky.data.repositories.place

import io.reactivex.Single
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.remote.services.PlaceApiService
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.data.repositories.place.mappers.PlaceApiListToPlaceListMapper


class PlaceRepositoryImpl(private val placeApiService: PlaceApiService): PlaceRepository {

    override fun getPlacesFrom1990(query: String): Single<List<Place>> {
        return placeApiService
                .fetchAllPlacesFrom1990(query)
                .map(PlaceApiListToPlaceListMapper())
    }
}