package pl.mobite.rocky.ui.components.map.processors

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Test
import pl.mobite.rocky.ui.components.map.MapAction.ReRenderAction
import pl.mobite.rocky.ui.components.map.MapResult.ReRenderResult
import pl.mobite.rocky.utils.assertMapResult


class ReRenderProcessorTest {

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

    private fun test(actions: List<ReRenderAction>, expectedResults: List<ReRenderResult>) {
        val processor = ReRenderProcessor()
        val testObserver = TestObserver<ReRenderResult>()

        processor.apply(Observable.fromIterable(actions)).subscribe(testObserver)

        testObserver.assertValueCount(expectedResults.size)

        testObserver.values().forEachIndexed {i, tested ->
            assertMapResult(expectedResults[i], tested)
        }

        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }
}