package pl.mobite.rocky.ui.components.map.processors

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.rocky.ui.components.map.MapAction.ReRenderAction
import pl.mobite.rocky.ui.components.map.MapResult.ReRenderResult


class ReRenderProcessor: ObservableTransformer<ReRenderAction, ReRenderResult> {

    override fun apply(actions: Observable<ReRenderAction>): ObservableSource<ReRenderResult> {
        return actions.switchMap {
            Observable.just(ReRenderResult)
        }
    }
}