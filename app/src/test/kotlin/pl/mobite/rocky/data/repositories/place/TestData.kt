package pl.mobite.rocky.data.repositories.place

import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.remote.models.CoordinatesApi
import pl.mobite.rocky.data.remote.models.LifeSpanApi
import pl.mobite.rocky.data.remote.models.PlaceApi

val dummyException = Exception("test exception")
const val dummyQuery = "query"

val dummyPlaceApi = createSamplePlaceAPI("Sample name", "14.2", "-12.3", "1993")
val dummyPlaceExpected = Place("Sample name", 1993, 14.2, -12.3)

val dummyPlaceApiInvalid1 = createSamplePlaceAPI(null, "14.2", "-12.3", "1993")
val dummyPlaceApiInvalid2 = createSamplePlaceAPI("Sample name 1", "14.2", "-12.3", null)
val dummyPlaceApiInvalid3 = createSamplePlaceAPI("Sample name 2", null, "-12.3", "1993")
val dummyPlaceApiInvalid4 = createSamplePlaceAPI("Sample name 3", "14.2", null, "1993")
val dummyPlaceApiInvalid5 = createSamplePlaceAPI("Sample name 4", CoordinatesApi("14.2", "-12.3"), null)
val dummyPlaceApiInvalid6 = createSamplePlaceAPI("Sample name 5", null, LifeSpanApi("1993", null, null))

val dummyPlaceApiList = listOf(dummyPlaceApiInvalid1, dummyPlaceApiInvalid2, dummyPlaceApi, dummyPlaceApiInvalid3)
val dummyPlaceListExpected = listOf(dummyPlaceExpected)

private fun createSamplePlaceAPI(name: String?, lat: String?, lng: String?, begin: String?) =
        createSamplePlaceAPI(name, CoordinatesApi(lat, lng), LifeSpanApi(begin, null, null))

private fun createSamplePlaceAPI(name: String?, coordinatesApi: CoordinatesApi?, lifeSpanApi: LifeSpanApi?) = PlaceApi(
        null,
        null,
        null,
        null,
        name,
        null,
        coordinatesApi,
        null,
        lifeSpanApi)

