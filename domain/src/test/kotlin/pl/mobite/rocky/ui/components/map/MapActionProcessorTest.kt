package pl.mobite.rocky.ui.components.map

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.mockito.Mockito.`when`
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.ui.components.map.MapAction.*
import pl.mobite.rocky.ui.components.map.MapResult.*
import pl.mobite.rocky.utils.ImmediateSchedulerProvider
import pl.mobite.rocky.utils.assertMapResult
import pl.mobite.rocky.utils.lazyMock

class MapActionProcessorTest {

    private val placeRepositoryMock: PlaceRepository by lazyMock()

    @Test
    fun testReRenderAction() {
        val mapAction = ReRenderAction
        val expectedResults = listOf(
                ReRenderResult
        )

        test(mapAction, expectedResults)
    }

    @Test
    fun testLoadPlacesActionSuccess() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(dummyPlaces))

        val mapAction = LoadPlacesAction(dummyQuery)
        val expectedResults = listOf(
                LoadPlacesResult.InFlight,
                LoadPlacesResult.Success(dummyPlaces, 0)
        )

        test(mapAction, expectedResults)
    }

    @Test
    fun testLoadPlacesActionSuccessButEmptyList() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(emptyList()))

        val mapAction = LoadPlacesAction(dummyQuery)
        val expectedResults = listOf(
                LoadPlacesResult.InFlight,
                LoadPlacesResult.Success(emptyList(), 0)
        )

        test(mapAction, expectedResults)
    }

    @Test
    fun testLoadPlacesActionFailure() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.error(dummyException))

        val mapAction = LoadPlacesAction(dummyQuery)
        val expectedResults = listOf(
                LoadPlacesResult.InFlight,
                LoadPlacesResult.Failure(dummyException)
        )

        test(mapAction, expectedResults)
    }

    @Test
    fun testClearSearchResultsAction() {
        val mapAction = ClearSearchResultsAction
        val expectedResults = listOf(
                ClearSearchResultsResult
        )

        test(mapAction, expectedResults)
    }

    private fun test(action: MapAction, expectedResults: List<MapResult>) {
        val processor = MapActionProcessor(placeRepositoryMock, ImmediateSchedulerProvider.instance)
        val testObserver = TestObserver<MapResult>()

        processor.apply(Observable.just(action)).subscribe(testObserver)

        testObserver.assertValueCount(expectedResults.size)

        testObserver.values().forEachIndexed {i, tested ->
            assertMapResult(expectedResults[i], tested)
        }

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

        private val dummyException = Throwable("dummy error")
    }
}