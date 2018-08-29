package pl.mobite.rocky.ui.map

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.ui.map.MapAction.*
import pl.mobite.rocky.ui.map.MapResult.*
import pl.mobite.rocky.utils.ImmediateSchedulerProvider
import pl.mobite.rocky.utils.lazyMock

class MapActionProcessorTest {

    private val placeRepositoryMock: PlaceRepository by lazyMock()

    private lateinit var mapActionProcessor: MapActionProcessor
    private lateinit var testObserver: TestObserver<MapResult>

    @Before
    fun setUp() {
        mapActionProcessor = MapActionProcessor(placeRepositoryMock, ImmediateSchedulerProvider.instance)
        testObserver = TestObserver()
    }

    @Test
    fun testMapReadyAction() {
        mapActionProcessor.apply(Observable.just(MapReadyAction))
                .subscribe(testObserver)

        testObserver.assertValueSequence(listOf(
                MapReadyResult
        ))
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testLoadPlacesActionSuccess() {
        `when`(placeRepositoryMock.getPlacesFrom1990(testQuery)).thenReturn(Single.just(places))

        mapActionProcessor.apply(Observable.just(LoadPlacesAction(testQuery)))
                .subscribe(testObserver)

        assertEquals(2, testObserver.valueCount())
        assertEquals(LoadPlacesResult.InFlight, testObserver.values()[0])
        assertTrue(testObserver.values()[1] is LoadPlacesResult.Success)
        assertEquals(places, (testObserver.values()[1] as LoadPlacesResult.Success).places)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testLoadPlacesActionSuccessButEmptyList() {
        `when`(placeRepositoryMock.getPlacesFrom1990(testQuery)).thenReturn(Single.just(emptyPlaces))

        mapActionProcessor.apply(Observable.just(LoadPlacesAction(testQuery)))
                .subscribe(testObserver)

        assertEquals(2, testObserver.valueCount())
        assertEquals(LoadPlacesResult.InFlight, testObserver.values()[0])
        assertTrue(testObserver.values()[1] is LoadPlacesResult.Success)
        assertEquals(emptyPlaces, (testObserver.values()[1] as LoadPlacesResult.Success).places)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testLoadPlacesActionFailure() {
        `when`(placeRepositoryMock.getPlacesFrom1990(testQuery)).thenReturn(Single.error(testError))

        mapActionProcessor.apply(Observable.just(LoadPlacesAction(testQuery)))
                .subscribe(testObserver)

        testObserver.assertValueSequence(listOf(
                LoadPlacesResult.InFlight,
                LoadPlacesResult.Failure(testError)
        ))
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testAllPlacesGoneAction() {
        mapActionProcessor.apply(Observable.just(AllPlacesGoneAction))
                .subscribe(testObserver)

        testObserver.assertValueSequence(listOf(
                AllPlacesGoneResult
        ))
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testErrorDisplayedAction() {
        mapActionProcessor.apply(Observable.just(ErrorDisplayedAction))
                .subscribe(testObserver)

        testObserver.assertValueSequence(listOf(
                ErrorDisplayedResult
        ))
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }
}