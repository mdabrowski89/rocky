package pl.mobite.rocky.data.remote.repository

import pl.mobite.rocky.data.remote.backend.responses.PlaceBackendResponse
import pl.mobite.rocky.data.repositories.models.Place


fun PlaceBackendResponse.toPlace(): Place? {
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