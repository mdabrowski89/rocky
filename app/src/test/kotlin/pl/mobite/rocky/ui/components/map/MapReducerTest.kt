package pl.mobite.rocky.ui.components.map

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class MapReducerTest {

    private lateinit var mapReducer: MapReducer
    private lateinit var initialState: MapViewState

    @Before
    fun setUp() {
        mapReducer = MapReducer()
        initialState = MapViewState.default()
    }

    @Test
    fun testReRenderResult() {
        val newState = mapReducer.apply(initialState, MapResult.ReRenderResult)

        assertEquals(initialState.markerDataList, newState.markerDataList)
        assertEquals(initialState.error, newState.error)
        assertEquals(initialState.isLoading, newState.isLoading)
        assertEquals(initialState.dataCreationTimestamp, newState.dataCreationTimestamp)
        assertEquals(!initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testLoadPlacesResultInFlight() {
        val newState = mapReducer.apply(initialState, MapResult.LoadPlacesResult.InFlight)

        assertEquals(initialState.markerDataList, newState.markerDataList)
        assertEquals(null, newState.error)
        assertEquals(true, newState.isLoading)
        assertEquals(initialState.dataCreationTimestamp, newState.dataCreationTimestamp)
        assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testLoadPlacesResultSuccess() {
        val newState = mapReducer.apply(initialState, MapResult.LoadPlacesResult.Success(dummyPlaces, dummyDataCreationTimestamp))

        assertMarkerDataListEquals(dummyMarkerDataList, newState.markerDataList)
        assertEquals(null, newState.error)
        assertEquals(false, newState.isLoading)
        assertEquals(dummyDataCreationTimestamp, newState.dataCreationTimestamp)
        assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testLoadPlacesResultSuccessButEmptyList() {
        val newState = mapReducer.apply(initialState, MapResult.LoadPlacesResult.Success(dummyEmptyPlaces, dummyDataCreationTimestamp))

        assertEquals(dummyEmptyMarkerDataList, newState.markerDataList)
        assertEquals(null, newState.error)
        assertEquals(false, newState.isLoading)
        assertEquals(dummyDataCreationTimestamp, newState.dataCreationTimestamp)
        assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testLoadPlacesResultFailure() {
        val newState = mapReducer.apply(initialState, MapResult.LoadPlacesResult.Failure(dummyException))

        assertEquals(initialState.markerDataList, newState.markerDataList)
        assertEquals(dummyException, newState.error?.throwable)
        assertEquals(false, newState.isLoading)
        assertEquals(initialState.dataCreationTimestamp, newState.dataCreationTimestamp)
        assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testClearSearchResultsResult() {
        val newState = mapReducer.apply(initialState, MapResult.ClearSearchResultsResult)

        assertEquals(dummyEmptyMarkerDataList, newState.markerDataList)
        assertEquals(initialState.error, newState.error)
        assertEquals(initialState.isLoading, newState.isLoading)
        assertEquals(null, newState.dataCreationTimestamp)
        assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }
}