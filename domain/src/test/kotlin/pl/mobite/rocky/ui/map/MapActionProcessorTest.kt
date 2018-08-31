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
    fun testMReRenderAction() {
        mapActionProcessor.apply(Observable.just(ReRenderAction))
                .subscribe(testObserver)

        testObserver.assertValueSequence(listOf(
                ReRenderResult
        ))
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testLoadPlacesActionSuccess() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(dummyPlaces))

        mapActionProcessor.apply(Observable.just(LoadPlacesAction(dummyQuery)))
                .subscribe(testObserver)

        assertEquals(2, testObserver.valueCount())
        assertEquals(LoadPlacesResult.InFlight, testObserver.values()[0])
        assertTrue(testObserver.values()[1] is LoadPlacesResult.Success)
        assertEquals(dummyPlaces, (testObserver.values()[1] as LoadPlacesResult.Success).places)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testLoadPlacesActionSuccessButEmptyList() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(dummyEmptyPlaces))

        mapActionProcessor.apply(Observable.just(LoadPlacesAction(dummyQuery)))
                .subscribe(testObserver)

        assertEquals(2, testObserver.valueCount())
        assertEquals(LoadPlacesResult.InFlight, testObserver.values()[0])
        assertTrue(testObserver.values()[1] is LoadPlacesResult.Success)
        assertEquals(dummyEmptyPlaces, (testObserver.values()[1] as LoadPlacesResult.Success).places)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testLoadPlacesActionFailure() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.error(dummyException))

        mapActionProcessor.apply(Observable.just(LoadPlacesAction(dummyQuery)))
                .subscribe(testObserver)

        testObserver.assertValueSequence(listOf(
                LoadPlacesResult.InFlight,
                LoadPlacesResult.Failure(dummyException)
        ))
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testClearSearchResultsAction() {
        mapActionProcessor.apply(Observable.just(ClearSearchResultsAction))
                .subscribe(testObserver)

        testObserver.assertValueSequence(listOf(
                ClearSearchResultsResult
        ))
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testClearErrorAction() {
        mapActionProcessor.apply(Observable.just(ClearErrorAction))
                .subscribe(testObserver)

        testObserver.assertValueSequence(listOf(
                ClearErrorResult
        ))
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }
}