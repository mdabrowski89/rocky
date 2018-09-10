package pl.mobite.rocky.ui.components.map

import org.junit.Before
import org.junit.Test
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.ui.components.map.MapResult.*
import pl.mobite.rocky.utils.StateTransformer
import pl.mobite.rocky.utils.assertMapViewState


class MapReducerTest {

    private lateinit var reducer: MapReducer

    @Before
    fun setUp() {
        reducer = MapReducer()
    }

    @Test
    fun testReRenderResult() {
        val mapResult = ReRenderResult
        val stateTransformer = MapViewState::reRender

        initialStates.forEach(test(mapResult, stateTransformer))
    }

    @Test
    fun testLoadPlacesResultInFlight() {
        val mapResult = LoadPlacesResult.InFlight
        val stateTransformer = MapViewState::loading

        initialStates.forEach(test(mapResult, stateTransformer))
    }

    @Test
    fun testLoadPlacesResultSuccess() {
        val mapResult = LoadPlacesResult.Success(dummyPlaces, dummyTimestamp)
        val stateTransformer = { initialState: MapViewState -> initialState.withData(dummyData, dummyTimestamp)}

        initialStates.forEach(test(mapResult, stateTransformer))
    }

    @Test
    fun testLoadPlacesResultSuccessButEmptyList() {
        val mapResult = LoadPlacesResult.Success(emptyList(), dummyTimestamp)
        val stateTransformer = { initialState: MapViewState -> initialState.withData(emptyList(), dummyTimestamp)}

        initialStates.forEach(test(mapResult, stateTransformer))
    }

    @Test
    fun testLoadPlacesResultFailure() {
        val mapResult = LoadPlacesResult.Failure(dummyThrowable)
        val stateTransformer = { initialState: MapViewState -> initialState.withError(dummyThrowable)}

        initialStates.forEach(test(mapResult, stateTransformer))
    }

    @Test
    fun testClearSearchResultsResult() {
        val mapResult = ClearSearchResultsResult
        val stateTransformer = MapViewState::clearData

        initialStates.forEach(test(mapResult, stateTransformer))
    }

    private fun test(result: MapResult, stateTransformer: StateTransformer<MapViewState>) = { initialState: MapViewState ->
        val expectedState = stateTransformer(initialState)
        val newState = reducer.apply(initialState, result)
        assertMapViewState(expectedState, newState)
    }

    companion object {

        private val dummyPlaces = listOf(
                Place("Test place 1", 1995, 12.4, 15.3),
                Place("Test place 2", 2000, -10.1, 18.1),
                Place("Test place 3", 2001, 15.7, 9.9)
        )

        private val dummyData = dummyPlaces.toMarkerDataList()

        private val dummyThrowable = Throwable("dummy error")

        private val dummyTimestamp = System.currentTimeMillis()

        private val initialStates = listOf(
                MapViewState.default(),
                MapViewState.default().loading(),
                MapViewState.default().withData(dummyData, dummyTimestamp),
                MapViewState.default().withData(emptyList(), dummyTimestamp),
                MapViewState.default().withError(dummyThrowable)
        )
    }
}