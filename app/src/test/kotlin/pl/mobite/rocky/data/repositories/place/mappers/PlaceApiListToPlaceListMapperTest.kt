package pl.mobite.rocky.data.repositories.place.mappers

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pl.mobite.rocky.data.repositories.place.dummyPlaceApiList
import pl.mobite.rocky.data.repositories.place.dummyPlaceListExpected

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
}
