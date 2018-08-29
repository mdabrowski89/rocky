package pl.mobite.rocky.data.remote.services

import io.reactivex.Observable
import io.reactivex.Single
import pl.mobite.rocky.data.remote.MusicBrainzService
import pl.mobite.rocky.data.remote.models.PlaceApi
import pl.mobite.rocky.data.remote.models.PlaceApiResponse
import pl.mobite.rocky.utils.PAGE_LIMIT
import java.util.concurrent.TimeUnit


class PlaceApiServiceImpl(
        private val musicBrainzServiceProvider: () -> MusicBrainzService,
        private val pageLimit: Int = PAGE_LIMIT,
        private val requestDelay: Long = 600
): PlaceApiService {

    /* provider lambda is used in order not to create service object only when it is needed */
    private val musicBrainzService by lazy { musicBrainzServiceProvider.invoke() }

    override fun fetchAllPlacesFrom1990(query: String): Single<List<PlaceApi>> {
        return musicBrainzService.getPlaces(query.withYearFilter(), 0, pageLimit)
                /* Create stream of request to fetch all pages */
                .flatMapObservable { placesApi ->
                    val list = mutableListOf(PlacesApiRequest(0, placesApi))
                    placesApi.count?.let { count ->
                        for (offset in pageLimit until count step pageLimit) {
                            list.add(PlacesApiRequest(offset))
                        }
                    }
                    Observable.fromIterable(list)
                }
                /* Return first page as it is already fetch and fetch all of the others */
                .concatMap { placesApiRequest ->
                    with(placesApiRequest) {
                        if (placeApiResponse != null) {
                            Observable.just(placeApiResponse.places ?: emptyList())
                        } else {
                            musicBrainzService.getPlaces(query.withYearFilter(), offset, pageLimit)
                                    .toObservable()
                                    /* delay between each requests is needed in order to not get throw out from the server */
                                    .delay(requestDelay, TimeUnit.MILLISECONDS)
                                    .map { placesApiResponse -> placesApiResponse.places ?: emptyList() }
                        }
                    }
                }
                /* Wait for all pages to be loaded */
                .toList()
                /* Connect all pages into one list of results */
                .map { tmpListOfListPlaces ->
                    val places = mutableListOf<PlaceApi>()
                    tmpListOfListPlaces.forEach{ tmpListOfPlaces -> tmpListOfPlaces.let { places.addAll(it.filterNotNull())} }
                    return@map places
                }
    }

    private fun String.withYearFilter() = "$this AND begin:[1990 TO 2200]"

    private data class PlacesApiRequest(val offset: Int, val placeApiResponse: PlaceApiResponse? = null)
}