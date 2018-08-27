package pl.mobite.rocky.utils

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer


class OneToOneObservableTransformer<Upstream, Downstream>(private val result: Downstream) : ObservableTransformer<Upstream, Downstream> {

    override fun apply(upstream: Observable<Upstream>): ObservableSource<Downstream> {
        return upstream.switchMap { Observable.just(result) }
    }
}