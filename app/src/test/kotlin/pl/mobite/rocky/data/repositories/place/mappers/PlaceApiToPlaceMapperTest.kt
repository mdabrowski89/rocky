package pl.mobite.rocky.data.repositories.place.mappers

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import pl.mobite.rocky.data.repositories.place.*

class PlaceApiToPlaceMapperTest {

    private lateinit var placeApiToPlaceMapper: PlaceApiToPlaceMapper

    @Before
    fun setUp() {
        placeApiToPlaceMapper = PlaceApiToPlaceMapper()
    }

    @Test
    fun testValidObject() {
        val placeTested = placeApiToPlaceMapper.apply(dummyPlaceApi)

        assertNotEquals(null, placeTested)
        assertEquals(dummyPlaceExpected.name, placeTested?.name)
        assertEquals(dummyPlaceExpected.openYear, placeTested?.openYear)
        assertEquals(dummyPlaceExpected.lat, placeTested?.lat)
        assertEquals(dummyPlaceExpected.lng, placeTested?.lng)
    }

    @Test
    fun testInvalidObjects() {
        assertEquals(null, placeApiToPlaceMapper.apply(dummyPlaceApiInvalid1))
        assertEquals(null, placeApiToPlaceMapper.apply(dummyPlaceApiInvalid2))
        assertEquals(null, placeApiToPlaceMapper.apply(dummyPlaceApiInvalid3))
        assertEquals(null, placeApiToPlaceMapper.apply(dummyPlaceApiInvalid4))
        assertEquals(null, placeApiToPlaceMapper.apply(dummyPlaceApiInvalid5))
        assertEquals(null, placeApiToPlaceMapper.apply(dummyPlaceApiInvalid6))
    }
}
