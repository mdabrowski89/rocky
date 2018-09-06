package pl.mobite.rocky.ui.components.map

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import pl.mobite.rocky.data.model.MarkerData
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.utils.ImmediateSchedulerProvider
import pl.mobite.rocky.utils.areMarkerDataListEquals
import pl.mobite.rocky.utils.lazyMock


class MapViewModelTest {

    private val placeRepositoryMock: PlaceRepository by lazyMock()

    private lateinit var mapViewModel: MapViewModel
    private lateinit var testObserver: TestObserver<MapViewState>

    private lateinit var initialState: MapViewState

    @Before
    fun setUp() {
        mapViewModel = MapViewModel(placeRepositoryMock, ImmediateSchedulerProvider.instance)
        testObserver = mapViewModel.states().test()
        initialState = MapViewState.default()
    }

    @Test
    fun testMapReadyIntent() {
        mapViewModel.processIntents(Observable.just(MapIntent.MapReadyIntent))

        testObserver.assertValueCount(2)
        testObserver.assertValueAt(0) { it == initialState }
        testObserver.assertValueAt(1) { with(it) {
            it == initialState.copy(reRenderFlag = !initialState.reRenderFlag)
        }}
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    @Test
    fun testSearchPlacesIntentSuccess() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(dummyPlaces))

        mapViewModel.processIntents(Observable.just(MapIntent.SearchPlacesIntent(dummyQuery)))

        testObserver.assertValueCount(3)
        testObserver.assertValueAt(0) { it == initialState }
        testObserver.assertValueAt(1) { with(it) { isLoading && error == null } }
        testObserver.assertValueAt(2) { with(it) {
            !isLoading && areMarkerDataListEquals(dummyMarkerDataList, markerDataList) && dataCreationTimestamp != null && error == null
        }}
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    @Test
    fun testSearchPlacesIntentSuccessAndAllMarkersGone() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(dummyPlaces))

        mapViewModel.processIntents(Observable.fromArray(
                MapIntent.SearchPlacesIntent(dummyQuery),
                MapIntent.AllMarkersGoneIntent
        ))

        testObserver.assertValueCount(4)
        testObserver.assertValueAt(0) { it == initialState }
        testObserver.assertValueAt(1) { with(it) { isLoading && error == null } }
        testObserver.assertValueAt(2) { with(it) {
            !isLoading && markerDataList == markerDataList && dataCreationTimestamp != null && error == null
        }}
        testObserver.assertValueAt(3) { with(it) {
            !isLoading && markerDataList == dummyEmptyMarkerDataList && dataCreationTimestamp == null && error == null
        }}
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    @Test
    fun testSearchPlacesIntentSuccessButEmptyList() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(dummyEmptyPlaces))

        mapViewModel.processIntents(Observable.just(MapIntent.SearchPlacesIntent(dummyQuery)))

        testObserver.assertValueCount(3)
        testObserver.assertValueAt(0) { it == initialState }
        testObserver.assertValueAt(1) { with(it) { isLoading && error == null } }
        testObserver.assertValueAt(2) { with(it) {
            !isLoading && markerDataList == dummyEmptyMarkerDataList && dataCreationTimestamp != null && error == null
        }}
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    @Test
    fun testSearchPlacesIntentFailure() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.error(dummyException))

        mapViewModel.processIntents(Observable.just(MapIntent.SearchPlacesIntent(dummyQuery)))

        testObserver.assertValueCount(3)
        testObserver.assertValueAt(0) { it == initialState }
        testObserver.assertValueAt(1) { with(it) { isLoading && error == null } }
        testObserver.assertValueAt(2) { with(it) {
            !isLoading && markerDataList == dummyEmptyMarkerDataList && dataCreationTimestamp == null && error?.throwable == dummyException
        }}
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    @Test
    fun testSearchPlacesIntentFailureAndErrorDisplayed() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.error(dummyException))

        mapViewModel.processIntents(Observable.fromArray(
                MapIntent.SearchPlacesIntent(dummyQuery)
        ))

        testObserver.assertValueCount(3)
        testObserver.assertValueAt(0) { it == initialState }
        testObserver.assertValueAt(1) { with(it) { isLoading && error == null } }
        testObserver.assertValueAt(2) { with(it) {
            !isLoading && markerDataList == dummyEmptyMarkerDataList && dataCreationTimestamp == null && error?.throwable == dummyException
        }}
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    /* Test the fallowing scenario
    * 1. user opens app
    * 2. user makes a query with results
    * 3. markers are displayed on map
    * 4. user makes a query with error
    * 5. all makers disappear
    * 6. error message disappear*/
    @Test
    fun testUsageScenario1() {
        val dummyQueryValid = dummyQuery
        val dummyQueryError = "error query"
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQueryValid)).thenReturn(Single.just(dummyPlaces))
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQueryError)).thenReturn(Single.error(dummyException))

        mapViewModel.processIntents(Observable.fromArray(
                MapIntent.MapReadyIntent,
                MapIntent.SearchPlacesIntent(dummyQueryValid),
                MapIntent.SearchPlacesIntent(dummyQueryError),
                MapIntent.AllMarkersGoneIntent
        ))

        testObserver.assertValueCount(7)
        testObserver.assertValueAt(0) { it == initialState }
        /* Map ready - re render state */
        testObserver.assertValueAt(1) { with(it) {
            it == initialState.copy(reRenderFlag = !initialState.reRenderFlag)
        }}
        /* First query loading */
        testObserver.assertValueAt(2) { with(it) { isLoading && error == null } }

        /* First query results */
        testObserver.assertValueAt(3) { with(it) {
            !isLoading && areMarkerDataListEquals(dummyMarkerDataList, markerDataList) && dataCreationTimestamp != null && error == null
        }}
        /* Second query loading */
        testObserver.assertValueAt(4) { with(it) {
            isLoading && areMarkerDataListEquals(dummyMarkerDataList, markerDataList) && dataCreationTimestamp != null && error == null
        }}
        /* Second query error */
        testObserver.assertValueAt(5) { with(it) {
            !isLoading && areMarkerDataListEquals(dummyMarkerDataList, markerDataList) && dataCreationTimestamp != null && error?.throwable == dummyException
        }}
        /* All makers disappear */
        testObserver.assertValueAt(6) { with(it) {
            !isLoading && markerDataList == dummyEmptyMarkerDataList && dataCreationTimestamp == null && error?.throwable == dummyException
        }}
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    companion object {

        private const val dummyQuery = "query"

        private val dummyPlaces = listOf(
                Place("Test place 1", 1995, 12.4, 15.3),
                Place("Test place 2", 2000, -10.1, 18.1),
                Place("Test place 3", 2001, 15.7, 9.9)
        )

        private val dummyEmptyPlaces = emptyList<Place>()

        private val dummyMarkerDataList = dummyPlaces.toMarkerDataList()

        private val dummyEmptyMarkerDataList = emptyList<MarkerData>()

        private val dummyException = Throwable("dummy error")
    }
}