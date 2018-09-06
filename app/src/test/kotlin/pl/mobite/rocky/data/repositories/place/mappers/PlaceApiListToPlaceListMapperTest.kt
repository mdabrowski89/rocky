package pl.mobite.rocky.data.repositories.place.mappers

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.remote.models.CoordinatesApi
import pl.mobite.rocky.data.remote.models.LifeSpanApi
import pl.mobite.rocky.utils.createSamplePlaceAPI

class PlaceApiListToPlaceListMapperTest {

    private lateinit var placeApiListToPlaceListMapper: PlaceApiListToPlaceListMapper

    @Before
    fun setUp() {
        placeApiListToPlaceListMapper = PlaceApiListToPlaceListMapper()
    }

    @Test
    fun testMapper() {
        val placeListTested = placeApiListToPlaceListMapper.apply(dummyPlaceApiList)

        assertEquals(dummyPlaceListExpected.size, placeListTested.size)
        placeListTested.forEachIndexed { index, place ->
            assertEquals(place, dummyPlaceListExpected[index])
        }
    }

    companion object {

        private val dummyPlaceApi = createSamplePlaceAPI("Sample name", "14.2", "-12.3", "1993")
        private val dummyPlaceExpected = Place("Sample name", 1993, 14.2, -12.3)

        private val dummyPlaceApiInvalid1 = createSamplePlaceAPI(null, "14.2", "-12.3", "1993")
        private val dummyPlaceApiInvalid2 = createSamplePlaceAPI("Sample name 1", "14.2", "-12.3", null)
        private val dummyPlaceApiInvalid3 = createSamplePlaceAPI("Sample name 2", null, "-12.3", "1993")
        private val dummyPlaceApiInvalid4 = createSamplePlaceAPI("Sample name 3", "14.2", null, "1993")
        private val dummyPlaceApiInvalid5 = createSamplePlaceAPI("Sample name 4", CoordinatesApi("14.2", "-12.3"), null)
        private val dummyPlaceApiInvalid6 = createSamplePlaceAPI("Sample name 5", null, LifeSpanApi("1993", null, null))

        private val dummyPlaceApiList = listOf(dummyPlaceApiInvalid1, dummyPlaceApiInvalid2, dummyPlaceApi,
                dummyPlaceApiInvalid3, dummyPlaceApiInvalid4, dummyPlaceApiInvalid5, dummyPlaceApiInvalid6)
        private val dummyPlaceListExpected = listOf(dummyPlaceExpected)
    }
}
