package pl.mobite.rocky.data.remote.repository

import pl.mobite.rocky.data.remote.backend.MusicBrainzBackend
import pl.mobite.rocky.data.remote.backend.responses.PlaceBackendResponse
import pl.mobite.rocky.data.remote.backend.responses.PlacesBackendResponse
import pl.mobite.rocky.data.repositories.models.Place
import pl.mobite.rocky.utils.PAGE_LIMIT
import java.lang.Thread.sleep


class PlaceRemoteRepositoryImpl(
    private val musicBrainzBackend: MusicBrainzBackend,
    private val pageLimit: Int = PAGE_LIMIT,
    private val requestDelay: Long = 600
): PlaceRemoteRepository {

    override fun fetchAllPlacesFrom1990(query: String): List<Place> {
        val response = musicBrainzBackend.getPlaces(query.withYearFilter(), 0, pageLimit)

        /* Create list of requests to fetch all pages */
        val requests = mutableListOf(PlacesBackendRequest(0, response))
        response.count?.let { count ->
            for (offset in pageLimit until count step pageLimit) {
                requests.add(PlacesBackendRequest(offset))
            }
        }

        /* Fetch all places */
        val tmpPlacesLists = requests.map { request ->
            if (request.placesBackendResponse != null) {
                request.placesBackendResponse.placeResponses ?: emptyList()
            } else {
                val requestResponse =
                    musicBrainzBackend.getPlaces(query.withYearFilter(), request.offset, pageLimit)
                sleep(requestDelay)
                requestResponse.placeResponses ?: emptyList()
            }
        }

        /* Connect all results into one list */
        val places = mutableListOf<PlaceBackendResponse>()
        tmpPlacesLists.forEach { tmpListOfPlaces -> tmpListOfPlaces.let { places.addAll(it.filterNotNull()) } }

        return places.mapNotNull { it.toPlace() }
    }

    private fun PlaceBackendResponse.toPlace(): Place? {
        val name = name
        val openYear = lifeSpan?.begin?.toIntOrNull()
        val lat = coordinatesResponse?.latitude?.toDoubleOrNull()
        val lng = coordinatesResponse?.longitude?.toDoubleOrNull()

        return if (name != null && openYear != null && lat != null && lng != null) {
            Place(name, openYear, lat, lng)
        } else {
            null
        }
    }
    private fun String.withYearFilter() = "$this AND begin:[1990 TO 2200]"

    private data class PlacesBackendRequest(val offset: Int, val placesBackendResponse: PlacesBackendResponse? = null)
}