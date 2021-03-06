package pl.mobite.rocky.ui.components.map

import io.reactivex.Observable
import org.junit.Test
import org.mockito.Mockito.`when`
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.data.repositories.models.Place
import pl.mobite.rocky.ui.components.map.MapIntent.*
import pl.mobite.rocky.utils.*


class MapViewModelTest {

    private val placeRepositoryMock: PlaceRepository by lazyMock()

    @Test
    fun testMapReadyIntent() {
        val intents = listOf(
            MapReadyIntent
        )
        val stateTransformers = listOf<MapViewStateModifier>(
            MapViewState::reRender
        )

        initialStates.forEach(test(intents, stateTransformers))
    }

    @Test
    fun testSearchPlacesIntentSuccess() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(dummyPlaces)

        val intents = listOf(
            SearchPlacesIntent(dummyQuery)
        )
        val stateTransformers = listOf<MapViewStateModifier>(
            { state -> state.loading() },
            { state -> state.withData(dummyData, dummyTimestamp) }
        )

        initialStates.forEach(test(intents, stateTransformers))
    }

    @Test
    fun testSearchPlacesIntentSuccessAndAllMarkersGone() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(dummyPlaces)

        val intents = listOf(
            SearchPlacesIntent(dummyQuery),
            AllMarkersGoneIntent
        )
        val stateTransformers = listOf<MapViewStateModifier>(
            { state -> state.loading() },
            { state -> state.withData(dummyData, dummyTimestamp) },
            { state -> state.clearData() }
        )

        initialStates.forEach(test(intents, stateTransformers))
    }

    @Test
    fun testSearchPlacesIntentSuccessButEmptyList() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(emptyList())

        val intents = listOf(
            SearchPlacesIntent(dummyQuery)
        )
        val stateTransformers = listOf<MapViewStateModifier>(
            { state -> state.loading() },
            { state -> state.withData(emptyList(), dummyTimestamp) }
        )

        initialStates.forEach(test(intents, stateTransformers))
    }

    @Test
    fun testSearchPlacesIntentFailure() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenThrow(dummyThrowable)

        val intents = listOf(
            SearchPlacesIntent(dummyQuery)
        )
        val stateTransformers = listOf<MapViewStateModifier>(
            { state -> state.loading() },
            { state -> state.withError(dummyThrowable) }
        )

        initialStates.forEach(test(intents, stateTransformers))
    }

    /* Test the fallowing scenario
    * 1. user opens app
    * 2. user makes a query with results
    * 3. markers are displayed on map
    * 4. user makes a query with error
    * 5. all makers disappear
    * 6. error message disappear */
    @Test
    fun testUsageScenario() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(dummyPlaces)
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery2)).thenThrow(dummyThrowable)

        val intents = listOf(
            MapReadyIntent,
            SearchPlacesIntent(dummyQuery),
            SearchPlacesIntent(dummyQuery2),
            AllMarkersGoneIntent
        )
        val stateTransformers = listOf<MapViewStateModifier>(
            { state -> state.reRender() },
            { state -> state.loading() },
            { state -> state.withData(dummyData, dummyTimestamp) },
            { state -> state.loading() },
            { state -> state.withError(dummyThrowable) },
            { state -> state.clearData() }
        )

        initialStates.forEach(test(intents, stateTransformers))
    }

    private fun test(intents: List<MapIntent>, stateTransformers: List<StateModifier<MapViewState>>) =
        { initialState: MapViewState ->
            val expectedStates = createExpectedStates(initialState, stateTransformers)
            test(initialState, intents, expectedStates)
        }

    private fun test(initialState: MapViewState, intents: List<MapIntent>, expectedStates: List<MapViewState>) {
        val viewModel = MapViewModel(placeRepositoryMock, ImmediateSchedulerProvider.instance, initialState)
        val testObserver = viewModel.states().test()

        viewModel.processIntents(Observable.fromIterable(intents))

        testObserver.assertValueCount(expectedStates.size)

        testObserver.values().forEachIndexed { i, tested ->
            assertMapViewState(expectedStates[i], tested, false)
        }
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    companion object {

        private const val dummyQuery = "query"
        private const val dummyQuery2 = "query2"

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