package pl.mobite.rocky.data.repositories.place

import io.reactivex.Single
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.remote.services.PlaceApiService
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.data.repositories.place.filters.PlaceApiByOpeningYearFilter
import pl.mobite.rocky.data.repositories.place.mappers.PlaceApiToPlaceListMapper


class PlaceRepositoryImpl: PlaceRepository {

    private val placesApiService by lazy { PlaceApiService() }

    override fun getPlacesFrom1990(query: String): Single<List<Place>> {
        val openingYearFilter = PlaceApiByOpeningYearFilter(1990)
        val placeListMapper = PlaceApiToPlaceListMapper()
        return placesApiService.fetchAllPlaces(query)
                /* Filter all places opened from 1990 */
                .map { placeApiList -> placeApiList.filter(openingYearFilter::test) }
                /* Map to domain model objects */
                .map(placeListMapper)
    }
}