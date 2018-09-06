package pl.mobite.rocky.data.repositories.place.mappers

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.remote.models.CoordinatesApi
import pl.mobite.rocky.data.remote.models.LifeSpanApi
import pl.mobite.rocky.utils.createSamplePlaceAPI

class PlaceApiToPlaceMapperTest {

    private lateinit var mapper: PlaceApiToPlaceMapper

    @Before
    fun setUp() {
        mapper = PlaceApiToPlaceMapper()
    }

    @Test
    fun testValidObject() {
        val placeTested = mapper.apply(dummyPlaceApi)

        assertNotEquals(null, placeTested)
        assertEquals(dummyPlaceExpected.name, placeTested?.name)
        assertEquals(dummyPlaceExpected.openYear, placeTested?.openYear)
        assertEquals(dummyPlaceExpected.lat, placeTested?.lat)
        assertEquals(dummyPlaceExpected.lng, placeTested?.lng)
    }

    @Test
    fun testInvalidObjects() {
        assertEquals(null, mapper.apply(dummyPlaceApiInvalid1))
        assertEquals(null, mapper.apply(dummyPlaceApiInvalid2))
        assertEquals(null, mapper.apply(dummyPlaceApiInvalid3))
        assertEquals(null, mapper.apply(dummyPlaceApiInvalid4))
        assertEquals(null, mapper.apply(dummyPlaceApiInvalid5))
        assertEquals(null, mapper.apply(dummyPlaceApiInvalid6))
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

    }
}
