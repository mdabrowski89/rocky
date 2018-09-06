package pl.mobite.rocky.ui.components.map

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pl.mobite.rocky.data.model.MarkerData
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.utils.assertMarkerDataListEquals


class MapReducerTest {

    private lateinit var reducer: MapReducer
    private lateinit var initialState: MapViewState

    @Before
    fun setUp() {
        reducer = MapReducer()
        initialState = MapViewState.default()
    }

    @Test
    fun testReRenderResult() {
        val newState = reducer.apply(initialState, MapResult.ReRenderResult)

        assertEquals(initialState.markerDataList, newState.markerDataList)
        assertEquals(initialState.error, newState.error)
        assertEquals(initialState.isLoading, newState.isLoading)
        assertEquals(initialState.dataCreationTimestamp, newState.dataCreationTimestamp)
        assertEquals(!initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testLoadPlacesResultInFlight() {
        val newState = reducer.apply(initialState, MapResult.LoadPlacesResult.InFlight)

        assertEquals(initialState.markerDataList, newState.markerDataList)
        assertEquals(null, newState.error)
        assertEquals(true, newState.isLoading)
        assertEquals(initialState.dataCreationTimestamp, newState.dataCreationTimestamp)
        assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testLoadPlacesResultSuccess() {
        val newState = reducer.apply(initialState, MapResult.LoadPlacesResult.Success(dummyPlaces, dummyDataCreationTimestamp))

        assertMarkerDataListEquals(dummyMarkerDataList, newState.markerDataList)
        assertEquals(null, newState.error)
        assertEquals(false, newState.isLoading)
        assertEquals(dummyDataCreationTimestamp, newState.dataCreationTimestamp)
        assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testLoadPlacesResultSuccessButEmptyList() {
        val newState = reducer.apply(initialState, MapResult.LoadPlacesResult.Success(dummyEmptyPlaces, dummyDataCreationTimestamp))

        assertEquals(dummyEmptyMarkerDataList, newState.markerDataList)
        assertEquals(null, newState.error)
        assertEquals(false, newState.isLoading)
        assertEquals(dummyDataCreationTimestamp, newState.dataCreationTimestamp)
        assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testLoadPlacesResultFailure() {
        val newState = reducer.apply(initialState, MapResult.LoadPlacesResult.Failure(dummyException))

        assertEquals(initialState.markerDataList, newState.markerDataList)
        assertEquals(dummyException, newState.error?.throwable)
        assertEquals(false, newState.isLoading)
        assertEquals(initialState.dataCreationTimestamp, newState.dataCreationTimestamp)
        assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testClearSearchResultsResult() {
        val newState = reducer.apply(initialState, MapResult.ClearSearchResultsResult)

        assertEquals(dummyEmptyMarkerDataList, newState.markerDataList)
        assertEquals(initialState.error, newState.error)
        assertEquals(initialState.isLoading, newState.isLoading)
        assertEquals(null, newState.dataCreationTimestamp)
        assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    companion object {

        private val dummyPlaces = listOf(
                Place("Test place 1", 1995, 12.4, 15.3),
                Place("Test place 2", 2000, -10.1, 18.1),
                Place("Test place 3", 2001, 15.7, 9.9)
        )

        private val dummyEmptyPlaces = emptyList<Place>()

        private val dummyMarkerDataList = dummyPlaces.toMarkerDataList()

        private val dummyEmptyMarkerDataList = emptyList<MarkerData>()

        private val dummyException = Throwable("dummy error")

        private val dummyDataCreationTimestamp = System.currentTimeMillis()

    }
}