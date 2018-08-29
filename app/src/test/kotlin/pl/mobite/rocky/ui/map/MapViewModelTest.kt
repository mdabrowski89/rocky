package pl.mobite.rocky.ui.map

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.utils.ImmediateSchedulerProvider
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
        `when`(placeRepositoryMock.getPlacesFrom1990(testQuery)).thenReturn(Single.just(places))

        mapViewModel.processIntents(Observable.just(MapIntent.SearchPlacesIntent(testQuery)))

        testObserver.assertValueCount(3)
        testObserver.assertValueAt(0) { it == initialState }
        testObserver.assertValueAt(1) { with(it) { isLoading && error == null } }
        testObserver.assertValueAt(2) { with(it) {
            !isLoading && places == places && placesTimestamp != null && error == null
        }}
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    @Test
    fun testSearchPlacesIntentSuccessAndAllPlacesGone() {
        `when`(placeRepositoryMock.getPlacesFrom1990(testQuery)).thenReturn(Single.just(places))

        mapViewModel.processIntents(Observable.fromArray(
                MapIntent.SearchPlacesIntent(testQuery),
                MapIntent.AllPlacesGoneIntent
        ))

        testObserver.assertValueCount(4)
        testObserver.assertValueAt(0) { it == initialState }
        testObserver.assertValueAt(1) { with(it) { isLoading && error == null } }
        testObserver.assertValueAt(2) { with(it) {
            !isLoading && places == places && placesTimestamp != null && error == null
        }}
        testObserver.assertValueAt(3) { with(it) {
            !isLoading && places == emptyPlaces && placesTimestamp == null && error == null
        }}
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    @Test
    fun testSearchPlacesIntentSuccessButEmptyList() {
        `when`(placeRepositoryMock.getPlacesFrom1990(testQuery)).thenReturn(Single.just(emptyPlaces))

        mapViewModel.processIntents(Observable.just(MapIntent.SearchPlacesIntent(testQuery)))

        testObserver.assertValueCount(3)
        testObserver.assertValueAt(0) { it == initialState }
        testObserver.assertValueAt(1) { with(it) { isLoading && error == null } }
        testObserver.assertValueAt(2) { with(it) {
            !isLoading && places == emptyPlaces && placesTimestamp != null && error == null
        }}
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    @Test
    fun testSearchPlacesIntentFailure() {
        `when`(placeRepositoryMock.getPlacesFrom1990(testQuery)).thenReturn(Single.error(testError))

        mapViewModel.processIntents(Observable.just(MapIntent.SearchPlacesIntent(testQuery)))

        testObserver.assertValueCount(3)
        testObserver.assertValueAt(0) { it == initialState }
        testObserver.assertValueAt(1) { with(it) { isLoading && error == null } }
        testObserver.assertValueAt(2) { with(it) {
            !isLoading && places == emptyPlaces && placesTimestamp == null && error == testError
        }}
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    @Test
    fun testSearchPlacesIntentFailureAndErrorDisplayed() {
        `when`(placeRepositoryMock.getPlacesFrom1990(testQuery)).thenReturn(Single.error(testError))

        mapViewModel.processIntents(Observable.fromArray(
                MapIntent.SearchPlacesIntent(testQuery),
                MapIntent.ErrorDisplayedIntent
        ))

        testObserver.assertValueCount(4)
        testObserver.assertValueAt(0) { it == initialState }
        testObserver.assertValueAt(1) { with(it) { isLoading && error == null } }
        testObserver.assertValueAt(2) { with(it) {
            !isLoading && places == emptyPlaces && placesTimestamp == null && error == testError
        }}
        testObserver.assertValueAt(3) { with(it) {
            !isLoading && places == emptyPlaces && placesTimestamp == null && error == null
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
        val validQuery = testQuery
        val errorQuery = "error query"
        `when`(placeRepositoryMock.getPlacesFrom1990(validQuery)).thenReturn(Single.just(places))
        `when`(placeRepositoryMock.getPlacesFrom1990(errorQuery)).thenReturn(Single.error(testError))

        mapViewModel.processIntents(Observable.fromArray(
                MapIntent.MapReadyIntent,
                MapIntent.SearchPlacesIntent(validQuery),
                MapIntent.SearchPlacesIntent(errorQuery),
                MapIntent.AllPlacesGoneIntent,
                MapIntent.ErrorDisplayedIntent
        ))

        testObserver.assertValueCount(8)
        testObserver.assertValueAt(0) { it == initialState }
        /* Map ready - re render state */
        testObserver.assertValueAt(1) { with(it) {
            it == initialState.copy(reRenderFlag = !initialState.reRenderFlag)
        }}
        /* First query loading */
        testObserver.assertValueAt(2) { with(it) { isLoading && error == null } }

        /* First query results */
        testObserver.assertValueAt(3) { with(it) {
            !isLoading && places == places && placesTimestamp != null && error == null
        }}
        /* Second query loading */
        testObserver.assertValueAt(4) { with(it) {
            isLoading && places == places && placesTimestamp != null && error == null
        }}
        /* Second query error */
        testObserver.assertValueAt(5) { with(it) {
            !isLoading && places == places && placesTimestamp != null && error == testError
        }}
        /* All makers disappear */
        testObserver.assertValueAt(6) { with(it) {
            !isLoading && places == emptyPlaces && placesTimestamp == null && error == testError
        }}
        /* Error message disappear */
        testObserver.assertValueAt(7) { with(it) {
            !isLoading && places == emptyPlaces && placesTimestamp == null && error == null
        }}
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }
}