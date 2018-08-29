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
        val placeTested = placeApiToPlaceMapper.apply(placeApi)

        assertNotEquals(null, placeTested)
        assertEquals(placeExpected.name, placeTested?.name)
        assertEquals(placeExpected.openYear, placeTested?.openYear)
        assertEquals(placeExpected.cords, placeTested?.cords)
    }

    @Test
    fun testInvalidObjects() {
        assertEquals(null, placeApiToPlaceMapper.apply(placeApiInvalid1))
        assertEquals(null, placeApiToPlaceMapper.apply(placeApiInvalid2))
        assertEquals(null, placeApiToPlaceMapper.apply(placeApiInvalid3))
        assertEquals(null, placeApiToPlaceMapper.apply(placeApiInvalid4))
        assertEquals(null, placeApiToPlaceMapper.apply(placeApiInvalid5))
        assertEquals(null, placeApiToPlaceMapper.apply(placeApiInvalid6))
    }
}
