package pl.mobite.rocky.data.remote.repository

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import pl.mobite.rocky.data.remote.backend.MusicBrainzBackend
import pl.mobite.rocky.data.remote.backend.responses.PlaceBackendResponse
import pl.mobite.rocky.data.remote.backend.responses.PlacesBackendResponse
import pl.mobite.rocky.data.repositories.models.Place
import pl.mobite.rocky.utils.PAGE_LIMIT
import java.util.concurrent.TimeUnit


class PlaceRemoteRepositoryImpl(
        private val musicBrainzBackendProvider: () -> MusicBrainzBackend,
        private val pageLimit: Int = PAGE_LIMIT,
        private val requestDelay: Long = 600
): PlaceRemoteRepository {

    /* provider lambda is used in order not to create backend object only when it is needed */
    private val musicBrainzBackend by lazy { musicBrainzBackendProvider.invoke() }

    override fun fetchAllPlacesFrom1990(query: String): Single<List<Place>> {
        return musicBrainzBackend.getPlaces(query.withYearFilter(), 0, pageLimit)
                /* Create stream of request to fetch all pages */
                .flatMapObservable { placesBackendResponse ->
                    val list = mutableListOf(
                        PlacesBackendRequest(
                            0,
                            placesBackendResponse
                        )
                    )
                    placesBackendResponse.count?.let { count ->
                        for (offset in pageLimit until count step pageLimit) {
                            list.add(PlacesBackendRequest(offset))
                        }
                    }
                    Observable.fromIterable(list)
                }
                /* Return first page as it is already fetch and fetch all of the others */
                .concatMap { placesBackendRequest ->
                    with(placesBackendRequest) {
                        if (placesBackendResponse != null) {
                            Observable.just(placesBackendResponse.placeResponses ?: emptyList())
                        } else {
                            musicBrainzBackend.getPlaces(query.withYearFilter(), offset, pageLimit)
                                    .toObservable()
                                    /* delay between each requests is needed in order to not get throw out from the server */
                                    .delay(requestDelay, TimeUnit.MILLISECONDS)
                                    .map { placesBackendResponse -> placesBackendResponse.placeResponses ?: emptyList() }
                        }
                    }
                }
                /* Wait for all pages to be loaded */
                .toList()
                /* Connect all pages into one list of results */
                .map { tmpListOfListPlaces ->
                    val places = mutableListOf<PlaceBackendResponse>()
                    tmpListOfListPlaces.forEach{ tmpListOfPlaces -> tmpListOfPlaces.let { places.addAll(it.filterNotNull())} }
                    return@map places
                }.map { placeBackendResponseListToPlaceListMapper.apply(it) }
    }

    private fun String.withYearFilter() = "$this AND begin:[1990 TO 2200]"

    private data class PlacesBackendRequest(val offset: Int, val placesBackendResponse: PlacesBackendResponse? = null)
    
    private val placeBackendResponseListToPlaceListMapper = Function<List<PlaceBackendResponse>, List<Place>> { placeBackendResponseList ->
        placeBackendResponseList.mapNotNull { placeBackendResponseToPlaceMapper.apply(it) }
    }
    
    private val placeBackendResponseToPlaceMapper = Function<PlaceBackendResponse, Place> { placeBackendResponse ->
        val name = placeBackendResponse.name
        val openYear = placeBackendResponse.lifeSpan?.begin?.toIntOrNull()
        val lat = placeBackendResponse.coordinatesResponse?.latitude?.toDoubleOrNull()
        val lng = placeBackendResponse.coordinatesResponse?.longitude?.toDoubleOrNull()
        
        if (name != null && openYear != null && lat != null && lng != null) {
            Place(name, openYear, lat, lng)
        } else {
            null
        }
    }
}