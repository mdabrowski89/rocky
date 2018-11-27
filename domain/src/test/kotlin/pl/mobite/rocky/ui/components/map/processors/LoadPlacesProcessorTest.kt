package pl.mobite.rocky.ui.components.map.processors

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.mockito.Mockito
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.data.repositories.models.Place
import pl.mobite.rocky.ui.components.map.MapAction.LoadPlacesAction
import pl.mobite.rocky.ui.components.map.MapResult.LoadPlacesResult
import pl.mobite.rocky.utils.ImmediateSchedulerProvider
import pl.mobite.rocky.utils.assertMapResult
import pl.mobite.rocky.utils.lazyMock


class LoadPlacesProcessorTest {

    private val placeRepositoryMock: PlaceRepository by lazyMock()

    @Test
    fun testLoadPlacesActionSuccess() {
        Mockito.`when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(dummyPlaces)

        val actions = listOf(
            LoadPlacesAction(dummyQuery)
        )
        val expectedResults = listOf(
            LoadPlacesResult.InFlight,
            LoadPlacesResult.Success(dummyPlaces, 0)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testLoadPlacesActionSuccessButEmptyList() {
        Mockito.`when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenReturn(emptyList())

        val actions = listOf(
            LoadPlacesAction(dummyQuery)
        )
        val expectedResults = listOf(
            LoadPlacesResult.InFlight,
            LoadPlacesResult.Success(emptyList(), 0)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testLoadPlacesActionFailure() {
        Mockito.`when`(placeRepositoryMock.getPlacesFrom1990(dummyQuery)).thenThrow(dummyException)

        val actions = listOf(
            LoadPlacesAction(dummyQuery)
        )
        val expectedResults = listOf(
            LoadPlacesResult.InFlight,
            LoadPlacesResult.Failure(dummyException)
        )

        test(actions, expectedResults)
    }

    private fun test(actions: List<LoadPlacesAction>, expectedResults: List<LoadPlacesResult>) {
        val processor = LoadPlacesProcessor(placeRepositoryMock, ImmediateSchedulerProvider.instance)
        val testObserver = TestObserver<LoadPlacesResult>()

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