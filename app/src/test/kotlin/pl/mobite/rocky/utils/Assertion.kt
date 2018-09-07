package pl.mobite.rocky.utils

import org.junit.Assert.*
import pl.mobite.rocky.data.model.MarkerData
import pl.mobite.rocky.ui.components.map.MapViewState


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

/**
 * Custom assert function for MapViewState
 */
fun assertMapViewState(expected: MapViewState, tested: MapViewState, assertExactTimestamp: Boolean = true) {
    assertMarkerDataListEquals(expected.markerDataList, tested.markerDataList)
    assertEquals("asd", expected.error?.throwable, tested.error?.throwable)
    assertEquals(expected.isLoading, tested.isLoading)
    when {
        assertExactTimestamp -> assertEquals(expected.dataCreationTimestamp, tested.dataCreationTimestamp)
        expected.dataCreationTimestamp == null -> assertNull(tested.dataCreationTimestamp)
        else -> assertNotNull(tested.dataCreationTimestamp)
    }
    assertEquals(expected.reRenderFlag, tested.reRenderFlag)
}