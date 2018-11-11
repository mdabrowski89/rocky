package pl.mobite.rocky.utils

import pl.mobite.rocky.data.remote.backend.responses.CoordinatesBackendResponse
import pl.mobite.rocky.data.remote.backend.responses.LifeSpanBackendResponse
import pl.mobite.rocky.data.remote.backend.responses.PlaceBackendResponse


fun createSamplePlaceAPI(name: String?, lat: String?, lng: String?, begin: String?) =
        createSamplePlaceAPI(name,
            CoordinatesBackendResponse(lat, lng),
            LifeSpanBackendResponse(begin, null, null)
        )

fun createSamplePlaceAPI(name: String?, coordinatesBackendResponse: CoordinatesBackendResponse?, lifeSpanBackendResponse: LifeSpanBackendResponse?) =
    PlaceBackendResponse(
        null, null, null, null, name, null, coordinatesBackendResponse, null, lifeSpanBackendResponse
    )