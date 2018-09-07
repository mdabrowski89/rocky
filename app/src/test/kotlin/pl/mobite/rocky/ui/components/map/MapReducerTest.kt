package pl.mobite.rocky.ui.components.map

import org.junit.Before
import org.junit.Test
import pl.mobite.rocky.data.model.MarkerData
import pl.mobite.rocky.data.model.ViewStateError
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.ui.components.map.MapResult.*
import pl.mobite.rocky.utils.assertMapViewState


class MapReducerTest {

    private lateinit var reducer: MapReducer

    @Before
    fun setUp() {
        reducer = MapReducer()
    }

    @Test
    fun testReRenderResult() {
        initialStates.forEach { initialState ->
            testReRenderResult(initialState)
        }
    }

    @Test
    fun testLoadPlacesResultInFlight() {
        initialStates.forEach { initialState ->
            testLoadPlacesResultInFlight(initialState)
        }
    }

    @Test
    fun testLoadPlacesResultSuccess() {
        initialStates.forEach { initialState ->
            testLoadPlacesResultSuccess(initialState)
        }
    }

    @Test
    fun testLoadPlacesResultSuccessButEmptyList() {
        initialStates.forEach { initialState ->
            testLoadPlacesResultSuccessButEmptyList(initialState)
        }
    }

    @Test
    fun testLoadPlacesResultFailure() {
        initialStates.forEach { initialState ->
            testLoadPlacesResultFailure(initialState)
        }
    }

    @Test
    fun testClearSearchResultsResult() {
        initialStates.forEach { initialState ->
            testClearSearchResultsResult(initialState)
        }
    }

    private fun testReRenderResult(initialState: MapViewState) {
        val expectedState = initialState.copy(
                reRenderFlag = !initialState.reRenderFlag
        )
        testMapReducer(initialState, ReRenderResult, expectedState)
    }

    private fun testLoadPlacesResultInFlight(initialState: MapViewState) {
        val expectedState = initialState.copy(
                error = null,
                isLoading = true
        )
        testMapReducer(initialState, LoadPlacesResult.InFlight, expectedState)
    }

    private fun testLoadPlacesResultSuccess(initialState: MapViewState) {
        val expectedState = initialState.copy(
                markerDataList = dummyList,
                error = null,
                isLoading = false,
                dataCreationTimestamp = dummyTimestamp
        )
        testMapReducer(initialState, LoadPlacesResult.Success(dummyPlaces, dummyTimestamp), expectedState)
    }

    private fun testLoadPlacesResultSuccessButEmptyList(initialState: MapViewState) {
        val expectedState = initialState.copy(
                markerDataList = dummyEmptyList,
                error = null,
                isLoading = false,
                dataCreationTimestamp = dummyTimestamp
        )
        testMapReducer(initialState, LoadPlacesResult.Success(dummyEmptyPlaces, dummyTimestamp), expectedState)
    }

    private fun testLoadPlacesResultFailure(initialState: MapViewState) {
        val expectedState = initialState.copy(
                error = ViewStateError(dummyException),
                isLoading = false
        )
        testMapReducer(initialState, LoadPlacesResult.Failure(dummyException), expectedState)
    }

    private fun testClearSearchResultsResult(initialState: MapViewState) {
        val expectedState = initialState.copy(
                markerDataList = dummyEmptyList,
                dataCreationTimestamp = null
        )
        testMapReducer(initialState, ClearSearchResultsResult, expectedState)
    }

    private fun testMapReducer(initialState: MapViewState, mapResult: MapResult, expectedState: MapViewState) {
        val newState = reducer.apply(initialState, mapResult)
        assertMapViewState(expectedState, newState)
    }

    companion object {

        private val dummyPlaces = listOf(
                Place("Test place 1", 1995, 12.4, 15.3),
                Place("Test place 2", 2000, -10.1, 18.1),
                Place("Test place 3", 2001, 15.7, 9.9)
        )

        private val dummyEmptyPlaces = emptyList<Place>()

        private val dummyList = dummyPlaces.toMarkerDataList()

        private val dummyEmptyList = emptyList<MarkerData>()

        private val dummyException = Throwable("dummy error")

        private val dummyTimestamp = System.currentTimeMillis()

        private val initialStates = listOf(
                MapViewState.default(),
                MapViewState.default().copy(
                        isLoading = true
                ),
                MapViewState.default().copy(
                        markerDataList = dummyList,
                        dataCreationTimestamp = dummyTimestamp
                ),
                MapViewState.default().copy(
                        markerDataList = dummyEmptyList,
                        dataCreationTimestamp = dummyTimestamp
                ),
                MapViewState.default().copy(
                        error = ViewStateError(dummyException)
                )
        )
    }
}