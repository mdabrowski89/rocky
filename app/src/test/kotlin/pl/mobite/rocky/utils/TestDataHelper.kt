package pl.mobite.rocky.utils

import pl.mobite.rocky.data.remote.models.CoordinatesApi
import pl.mobite.rocky.data.remote.models.LifeSpanApi
import pl.mobite.rocky.data.remote.models.PlaceApi


fun createSamplePlaceAPI(name: String?, lat: String?, lng: String?, begin: String?) =
        createSamplePlaceAPI(name, CoordinatesApi(lat, lng), LifeSpanApi(begin, null, null))

fun createSamplePlaceAPI(name: String?, coordinatesApi: CoordinatesApi?, lifeSpanApi: LifeSpanApi?) = PlaceApi(
        null,
        null,
        null,
        null,
        name,
        null,
        coordinatesApi,
        null,
        lifeSpanApi)