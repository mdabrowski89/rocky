package pl.mobite.rocky.ui.components.map

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.ui.components.map.MapAction.*
import pl.mobite.rocky.ui.components.map.MapResult.*
import pl.mobite.rocky.utils.ImmediateSchedulerProvider
import pl.mobite.rocky.utils.lazyMock

class MapActionProcessorTest {

    private val placeRepositoryMock: PlaceRepository by lazyMock()

    private lateinit var processor: MapActionProcessor
    private lateinit var testObserver: TestObserver<MapResult>

    @Before
    fun setUp() {
        processor = MapActionProcessor(placeRepositoryMock, ImmediateSchedulerProvider.instance)
        testObserver = TestObserver()
    }

    @Test
    fun testReRenderAction() {
        processor.apply(Observable.just(ReRenderAction))
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

        processor.apply(Observable.just(LoadPlacesAction(dummyQuery)))
                .subscribe(testObserver)

        testObserver.assertValueCount(2)
        assertEquals(LoadPlacesResult.InFlight, testObserver.values()[0])
        assertTrue(testObserver.values()[1] is LoadPlacesResult.Success)
        assertEquals(dummyPlaces, (testObserver.values()[1] as LoadPlacesResult.Success).places)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testLoadPlacesActionSuccessButEmptyList() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(dummyEmptyPlaces))

        processor.apply(Observable.just(LoadPlacesAction(dummyQuery)))
                .subscribe(testObserver)

        testObserver.assertValueCount(2)
        assertEquals(LoadPlacesResult.InFlight, testObserver.values()[0])
        assertTrue(testObserver.values()[1] is LoadPlacesResult.Success)
        assertEquals(dummyEmptyPlaces, (testObserver.values()[1] as LoadPlacesResult.Success).places)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testLoadPlacesActionFailure() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.error(dummyException))

        processor.apply(Observable.just(LoadPlacesAction(dummyQuery)))
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
        processor.apply(Observable.just(ClearSearchResultsAction))
                .subscribe(testObserver)

        testObserver.assertValueSequence(listOf(
                ClearSearchResultsResult
        ))
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    companion object {

        private const val dummyQuery = "query"

        private val dummyPlaces = listOf(
                Place("Test place 1", 1995, 12.4, 15.3),
                Place("Test place 2", 2000, -10.1, 18.1),
                Place("Test place 3", 2001, 15.7, 9.9)
        )

        private val dummyEmptyPlaces = emptyList<Place>()

        private val dummyException = Throwable("dummy error")
    }
}