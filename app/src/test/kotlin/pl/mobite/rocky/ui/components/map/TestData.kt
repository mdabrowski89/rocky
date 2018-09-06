package pl.mobite.rocky.ui.components.map

import org.junit.Assert.assertEquals
import pl.mobite.rocky.data.model.MarkerData
import pl.mobite.rocky.data.models.Place


const val dummyQuery = "query"

val dummyPlaces = listOf(
        Place("Test place 1", 1995, 12.4, 15.3),
        Place("Test place 2", 2000, -10.1, 18.1),
        Place("Test place 3", 2001, 15.7, 9.9)
)

val dummyEmptyPlaces = emptyList<Place>()

val dummyMarkerDataList = dummyPlaces.toMarkerDataList()

val dummyEmptyMarkerDataList = emptyList<MarkerData>()

val dummyException = Throwable("dummy error")

val dummyDataCreationTimestamp = System.currentTimeMillis()

/**
 * Custom assert function for testing markerDataList
 */
fun assertMarkerDataListEquals(expectedMarkerDataList: List<MarkerData>, testedMakerDataList: List<MarkerData>) {
    assertEquals(expectedMarkerDataList.size, testedMakerDataList.size)
    expectedMarkerDataList.forEachIndexed { i, expectedMarkerData ->
        val testedMarkerData = testedMakerDataList[i]
        assertEquals(expectedMarkerData.markerOptions.position.latitude, testedMarkerData.markerOptions.position.latitude, 0.0)
        assertEquals(expectedMarkerData.markerOptions.position.longitude, testedMarkerData.markerOptions.position.longitude, 0.0)
        assertEquals(expectedMarkerData.markerOptions.title, testedMarkerData.markerOptions.title)
        assertEquals(expectedMarkerData.description, testedMarkerData.description)
        assertEquals(expectedMarkerData.timeToLive, testedMarkerData.timeToLive)
    }
}

fun areMarkerDataListEquals(expectedMarkerDataList: List<MarkerData>, testedMakerDataList: List<MarkerData>): Boolean {
    if (expectedMarkerDataList.size == testedMakerDataList.size) {
        expectedMarkerDataList.forEachIndexed { i, expectedMarkerData ->
            val testedMarkerData = testedMakerDataList[i]
            val areEqual = expectedMarkerData.markerOptions.position.latitude == testedMarkerData.markerOptions.position.latitude
                    && expectedMarkerData.markerOptions.position.longitude == testedMarkerData.markerOptions.position.longitude
                    && expectedMarkerData.markerOptions.title == testedMarkerData.markerOptions.title
                    && expectedMarkerData.description == testedMarkerData.description
                    && expectedMarkerData.timeToLive == testedMarkerData.timeToLive
            if (!areEqual) {
                return false
            }
        }
        return true
    }
    return false
}