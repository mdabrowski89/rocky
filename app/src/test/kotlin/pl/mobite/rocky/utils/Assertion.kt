package pl.mobite.rocky.utils

import org.junit.Assert
import pl.mobite.rocky.data.model.MarkerData


/**
 * Custom assert function for testing markerDataList
 */
fun assertMarkerDataListEquals(expectedMarkerDataList: List<MarkerData>, testedMakerDataList: List<MarkerData>) {
    Assert.assertEquals(expectedMarkerDataList.size, testedMakerDataList.size)
    expectedMarkerDataList.forEachIndexed { i, expectedMarkerData ->
        val testedMarkerData = testedMakerDataList[i]
        Assert.assertEquals(expectedMarkerData.markerOptions.position.latitude, testedMarkerData.markerOptions.position.latitude, 0.0)
        Assert.assertEquals(expectedMarkerData.markerOptions.position.longitude, testedMarkerData.markerOptions.position.longitude, 0.0)
        Assert.assertEquals(expectedMarkerData.markerOptions.title, testedMarkerData.markerOptions.title)
        Assert.assertEquals(expectedMarkerData.description, testedMarkerData.description)
        Assert.assertEquals(expectedMarkerData.timeToLive, testedMarkerData.timeToLive)
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