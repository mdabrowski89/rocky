package pl.mobite.rocky.ui.components.map

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.mockito.Mockito.`when`
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.ui.components.map.MapAction.*
import pl.mobite.rocky.ui.components.map.MapResult.ClearSearchResultsResult
import pl.mobite.rocky.ui.components.map.MapResult.LoadPlacesResult.*
import pl.mobite.rocky.ui.components.map.MapResult.ReRenderResult
import pl.mobite.rocky.utils.ImmediateSchedulerProvider
import pl.mobite.rocky.utils.assertMapResult
import pl.mobite.rocky.utils.lazyMock

class MapActionProcessorTest {

    private val placeRepositoryMock: PlaceRepository by lazyMock()

    @Test
    fun testReRenderAction() {
        val actions = listOf(
                ReRenderAction
        )
        val expectedResults = listOf(
                ReRenderResult
        )

        test(actions, expectedResults)
    }

    @Test
    fun testLoadPlacesActionSuccess() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(dummyPlaces))

        val actions = listOf(
                LoadPlacesAction(dummyQuery)
        )
        val expectedResults = listOf(
                InFlight,
                Success(dummyPlaces, 0)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testLoadPlacesActionSuccessButEmptyList() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.just(emptyList()))

        val actions = listOf(
                LoadPlacesAction(dummyQuery)
        )
        val expectedResults = listOf(
                InFlight,
                Success(emptyList(), 0)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testLoadPlacesActionFailure() {
        `when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(Single.error(dummyException))

        val actions = listOf(
                LoadPlacesAction(dummyQuery)
        )
        val expectedResults = listOf(
                InFlight,
                Failure(dummyException)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testClearSearchResultsAction() {
        val actions = listOf(
                ClearSearchResultsAction
        )
        val expectedResults = listOf(
                ClearSearchResultsResult
        )

        test(actions, expectedResults)
    }

    private fun test(actions: List<MapAction>, expectedResults: List<MapResult>) {
        val processor = MapActionProcessor(placeRepositoryMock, ImmediateSchedulerProvider.instance)
        val testObserver = TestObserver<MapResult>()

        processor.apply(Observable.fromIterable(actions)).subscribe(testObserver)

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