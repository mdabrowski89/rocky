package pl.mobite.rocky.data.remote.services

import io.reactivex.Observable
import io.reactivex.Single
import pl.mobite.rocky.data.remote.MusicBrainzService
import pl.mobite.rocky.data.remote.MusicBrainzService.Companion.MAX_API_PAGE
import pl.mobite.rocky.data.remote.MusicBrainzService.Companion.PAGE_LIMIT
import pl.mobite.rocky.data.remote.models.PlaceApi
import pl.mobite.rocky.data.remote.models.PlaceApiResponse
import pl.mobite.rocky.data.remote.retrofit
import pl.mobite.rocky.utils.ToMuchDataToFetchException


class PlaceApiService {

    fun fetchAllPlaces(query: String): Single<List<PlaceApi>> {
        val service = retrofit.create(MusicBrainzService::class.java)
        return service.getPlaces(query)
                /* Create stream of request to fetch all pages */
                .flatMapObservable { placesApi ->
                    val list = mutableListOf(PlacesApiRequest(0, placesApi))
                    placesApi.count?.let { count ->
                        /* check if we are able to fetch the data */
                        if (count / PAGE_LIMIT > MAX_API_PAGE) {
                            return@flatMapObservable Observable.error<PlacesApiRequest>(ToMuchDataToFetchException())
                        }
                        for (offset in PAGE_LIMIT..count step PAGE_LIMIT) {
                            list.add(PlacesApiRequest(offset, null))
                        }
                    }
                    Observable.fromIterable(list)
                }
                /* Return first page as it is already fetch and fetch all of the others */
                .flatMapSingle { placesApiRequest ->
                    with(placesApiRequest) {
                        if (placeApiResponse != null) {
                            Single.just(placeApiResponse.places ?: emptyList())
                        } else {
                            service.getPlaces(query, offset)
                                    .map { placesApiResponse -> placesApiResponse.places ?: emptyList() }
                        }
                    }
                }
                /* Wait for all pages to be loaded */
                .toList()
                /* Connect all pages into one list of results */
                .map { tmpListOfList ->
                    val places = mutableListOf<PlaceApi>()
                    tmpListOfList.forEach{ tmpList -> tmpList.let { places.addAll(it.filterNotNull())} }
                    return@map places
                }
    }

    private data class PlacesApiRequest(val offset: Int, val placeApiResponse: PlaceApiResponse?)
}