package pl.mobite.rocky.ui.components.map.processors

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.rocky.ui.components.map.MapAction.ClearSearchResultsAction
import pl.mobite.rocky.ui.components.map.MapResult.ClearSearchResultsResult


class ClearSearchResultsProcessor: ObservableTransformer<ClearSearchResultsAction, ClearSearchResultsResult> {

    override fun apply(actions: Observable<ClearSearchResultsAction>): ObservableSource<ClearSearchResultsResult> {
        return actions.switchMap {
            Observable.just(ClearSearchResultsResult)
        }
    }
}