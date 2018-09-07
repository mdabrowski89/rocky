package pl.mobite.rocky.ui.components.map

import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Test
import org.mockito.Mockito.`when`
import pl.mobite.rocky.data.model.MarkerData
import pl.mobite.rocky.data.model.ViewStateError
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.ui.components.map.MapIntent.*
import pl.mobite.rocky.utils.ImmediateSchedulerProvider
import pl.mobite.rocky.utils.assertMapViewState
import pl.mobite.rocky.utils.lazyMock


class MapViewModelTest {

    private val placeRepositoryMock: PlaceRepository by lazyMock()

    @Test
    fun testMapReadyIntent() {
        val intents = listOf<MapIntent>(
                MapReadyIntent
        )

        initialStates.forEach { initialState ->
            testMapReadyIntent(initialState, intents)
        }
    }

    @Test
    fun testSearchPlacesIntentSuccess() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(dummyPlaces))
        val intents = listOf<MapIntent>(
                SearchPlacesIntent(dummyQuery)
        )

        initialStates.forEach { initialState ->
            testSearchPlacesIntentSuccess(initialState, intents)
        }
    }

    @Test
    fun testSearchPlacesIntentSuccessAndAllMarkersGone() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(dummyPlaces))
        val intents = listOf (
                SearchPlacesIntent(dummyQuery),
                AllMarkersGoneIntent
        )

        initialStates.forEach { initialState ->
            testSearchPlacesIntentSuccessAndAllMarkersGone(initialState, intents)
        }
    }

    @Test
    fun testSearchPlacesIntentSuccessButEmptyList() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(dummyEmptyPlaces))
        val intents = listOf<MapIntent>(
                SearchPlacesIntent(dummyQuery)
        )

        initialStates.forEach { initialState ->
            testSearchPlacesIntentSuccessButEmptyList(initialState, intents)
        }
    }

    @Test
    fun testSearchPlacesIntentFailure() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.error(dummyException))
        val intents = listOf<MapIntent>(
                SearchPlacesIntent(dummyQuery)
        )

        initialStates.forEach { initialState ->
            testSearchPlacesIntentFailure(initialState, intents)
        }
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
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(dummyPlaces))
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery2)).thenReturn(Single.error(dummyException))
        val intents = listOf(
                MapReadyIntent,
                SearchPlacesIntent(dummyQuery),
                SearchPlacesIntent(dummyQuery2),
                AllMarkersGoneIntent
        )

        initialStates.forEach { initialState ->
            testUsageScenario(initialState, intents)
        }
    }

    private fun testMapReadyIntent(initialState: MapViewState, intents: List<MapIntent>) {
        val expectedStates = listOf(
                initialState,
                initialState.copy(
                        reRenderFlag = !initialState.reRenderFlag
                )
        )

        testViewModel(initialState, intents, expectedStates)
    }

    private fun testSearchPlacesIntentSuccess(initialState: MapViewState, intents: List<MapIntent>) {
        val expectedStates = listOf(
                initialState,
                initialState.copy(
                        isLoading = true,
                        error = null
                ),
                initialState.copy(
                        isLoading = false,
                        markerDataList = dummyList,
                        dataCreationTimestamp = dummyTimestamp,
                        error = null
                )
        )

        testViewModel(initialState, intents, expectedStates)
    }

    private fun testSearchPlacesIntentSuccessAndAllMarkersGone(initialState: MapViewState, intents: List<MapIntent>) {
        val expectedStates = listOf(
                initialState,
                initialState.copy(
                        isLoading = true,
                        error = null
                ),
                initialState.copy(
                        isLoading = false,
                        markerDataList = dummyList,
                        dataCreationTimestamp = dummyTimestamp,
                        error = null
                ),
                initialState.copy(
                        isLoading = false,
                        markerDataList = emptyList(),
                        dataCreationTimestamp = null,
                        error = null
                )
        )

        testViewModel(initialState, intents, expectedStates)
    }

    private fun testSearchPlacesIntentSuccessButEmptyList(initialState: MapViewState, intents: List<MapIntent>) {
        val expectedStates = listOf(
                initialState,
                initialState.copy(
                        isLoading = true,
                        error = null
                ),
                initialState.copy(
                        isLoading = false,
                        markerDataList = dummyEmptyList,
                        dataCreationTimestamp = dummyTimestamp,
                        error = null
                )
        )
        testViewModel(initialState, intents, expectedStates)
    }

    private fun testSearchPlacesIntentFailure(initialState: MapViewState, intents: List<MapIntent>) {
        val expectedStates = listOf(
                initialState,
                initialState.copy(
                        isLoading = true,
                        error = null
                ),
                initialState.copy(
                        isLoading = false,
                        error = ViewStateError(dummyException)
                )
        )
        testViewModel(initialState, intents, expectedStates)
    }

    private fun testUsageScenario(initialState: MapViewState, intents: List<MapIntent>) {
        val expectedStates = listOf(
                initialState,
                /* Map ready - re render state */
                initialState.copy(
                        reRenderFlag = !initialState.reRenderFlag
                ),
                /* First query loading */
                initialState.copy(
                        reRenderFlag = !initialState.reRenderFlag,
                        isLoading = true,
                        error = null
                ),
                /* First query results */
                initialState.copy(
                        reRenderFlag = !initialState.reRenderFlag,
                        isLoading = false,
                        markerDataList = dummyList,
                        dataCreationTimestamp = dummyTimestamp,
                        error = null
                ),
                /* Second query loading - results still from first query */
                initialState.copy(
                        reRenderFlag = !initialState.reRenderFlag,
                        isLoading = true,
                        markerDataList = dummyList,
                        dataCreationTimestamp = dummyTimestamp,
                        error = null
                ),
                /* Second query error - results still from first query */
                initialState.copy(
                        reRenderFlag = !initialState.reRenderFlag,
                        isLoading = false,
                        markerDataList = dummyList,
                        dataCreationTimestamp = dummyTimestamp,
                        error = ViewStateError(dummyException)
                ),
                /* All makers disappear */
                initialState.copy(
                        reRenderFlag = !initialState.reRenderFlag,
                        isLoading = false,
                        markerDataList = dummyEmptyList,
                        dataCreationTimestamp = null,
                        error = ViewStateError(dummyException)
                )
        )
        testViewModel(initialState, intents, expectedStates)
    }

    private fun testViewModel(initialState: MapViewState, intents: List<MapIntent>, expectedStates: List<MapViewState>) {
        val viewModel = MapViewModel(placeRepositoryMock, ImmediateSchedulerProvider.instance, initialState)
        val testObserver = viewModel.states().test()

        val expectedStatesDistinct = expectedStates.distinctUntilChanged()

        viewModel.processIntents(Observable.fromIterable(intents))

        testObserver.assertValueCount(expectedStatesDistinct.size)

        expectedStatesDistinct.forEachIndexed {i, expectedState ->
            assertMapViewState(expectedState, testObserver.values()[i], false)
        }
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    private fun <T> List<T>.distinctUntilChanged(): List<T> {
        val distinctUtilChangedList = mutableListOf<T>()
        forEachIndexed { i, item ->
            val prevItem = getOrNull(i -1)
            if (prevItem == null || prevItem != item) {
                distinctUtilChangedList.add(item)
            }
        }
        return distinctUtilChangedList
    }

    companion object {

        private const val dummyQuery = "query"
        private const val dummyQuery2 = "query2"

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