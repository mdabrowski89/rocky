package pl.mobite.rocky.data.repositories.place

import org.junit.Before
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.models.PlaceCords
import pl.mobite.rocky.data.remote.models.CoordinatesApi
import pl.mobite.rocky.data.remote.models.LifeSpanApi
import pl.mobite.rocky.data.remote.models.PlaceApi

val testException = Exception("test exception")
const val testQuery = "test query"

val placeApi = createSamplePlaceAPI("Sample name", "14.2", "-12.3", "1993")
val placeExpected = Place("Sample name", 1993, PlaceCords(14.2, -12.3))

val placeApiInvalid1 = createSamplePlaceAPI(null, "14.2", "-12.3", "1993")
val placeApiInvalid2 = createSamplePlaceAPI("Sample name 1", "14.2", "-12.3", null)
val placeApiInvalid3 = createSamplePlaceAPI("Sample name 1", null, "-12.3", "1993")
val placeApiInvalid4 = createSamplePlaceAPI("Sample name 1", "14.2", null, "1993")
val placeApiInvalid5 = createSamplePlaceAPI("Sample name 1", CoordinatesApi("14.2", "-12.3"), null)
val placeApiInvalid6 = createSamplePlaceAPI("Sample name 1", null, LifeSpanApi("1993", null, null))

val placeApiList = listOf(placeApiInvalid1, placeApiInvalid2, placeApi, placeApiInvalid3)
val placeListExpected = listOf(placeExpected)

@Before

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

