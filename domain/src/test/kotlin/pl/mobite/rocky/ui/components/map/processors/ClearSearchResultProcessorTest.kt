package pl.mobite.rocky.ui.components.map.processors

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Test
import pl.mobite.rocky.ui.components.map.MapAction.ClearSearchResultsAction
import pl.mobite.rocky.ui.components.map.MapResult.ClearSearchResultsResult
import pl.mobite.rocky.utils.assertMapResult


class ClearSearchResultProcessorTest {

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

    private fun test(actions: List<ClearSearchResultsAction>, expectedResults: List<ClearSearchResultsResult>) {
        val processor = ClearSearchResultsProcessor()
        val testObserver = TestObserver<ClearSearchResultsResult>()

        processor.apply(Observable.fromIterable(actions)).subscribe(testObserver)

        testObserver.assertValueCount(expectedResults.size)

        testObserver.values().forEachIndexed {i, tested ->
            assertMapResult(expectedResults[i], tested)
        }

        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }
}