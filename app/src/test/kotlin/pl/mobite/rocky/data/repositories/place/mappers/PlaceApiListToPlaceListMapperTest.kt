package pl.mobite.rocky.data.repositories.place.mappers

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pl.mobite.rocky.data.repositories.place.placeApiList
import pl.mobite.rocky.data.repositories.place.placeListExpected

class PlaceApiListToPlaceListMapperTest {

    private lateinit var placeApiListToPlaceListMapper: PlaceApiListToPlaceListMapper

    @Before
    fun setUp() {
        placeApiListToPlaceListMapper = PlaceApiListToPlaceListMapper()
    }

    @Test
    fun testMapper() {
        val placeListTested = placeApiListToPlaceListMapper.apply(placeApiList)

        assertEquals(placeListExpected.size, placeListTested.size)
        placeListTested.forEachIndexed { index, place ->
            assertEquals(place, placeListExpected[index])
        }
    }
}
