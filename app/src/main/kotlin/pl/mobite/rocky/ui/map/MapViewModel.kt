package pl.mobite.rocky.ui.map

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.repositories.PlacesRepository
import pl.mobite.rocky.ui.map.MapAction.*
import pl.mobite.rocky.ui.map.MapResult.*


class MapViewModel: ViewModel() {

    private val intentsRelay: PublishRelay<MapIntent> = PublishRelay.create()
    private val statesObserver: Observable<MapViewState> = createStatesObserver()

    private fun createStatesObserver() = intentsRelay
                .map { intent -> actionFromIntent(intent)}
                .compose(actionProcessor)
                .scan(MapViewState.default(), reducer)
                .distinctUntilChanged()
                .replay(1)
                .autoConnect(0)

    fun processIntents(intents: Observable<MapIntent>) {
        intents.subscribe(intentsRelay)
    }

    fun states(): Observable<MapViewState> {
        return statesObserver
    }
}

private fun actionFromIntent(intent: MapIntent): MapAction {
    return when(intent) {
        is MapIntent.MapReadyIntent -> MapReadyAction
        is MapIntent.SearchPlacesIntent -> LoadPlacesAction(intent.query)
        is MapIntent.AllPlacesGoneIntent -> AllPlacesGoneAction
        is MapIntent.ErrorDisplayedIntent -> ErrorDisplayedAction
    }
}

private val placesRepository = PlacesRepository()

var actionProcessor: ObservableTransformer<MapAction, MapResult> =
        ObservableTransformer<MapAction, MapResult> { actions: Observable<MapAction> ->
            actions.publish { shared ->
                Observable.merge(listOf(
                        shared.ofType(MapReadyAction::class.java).compose(mapReadyProcessor),
                        shared.ofType(LoadPlacesAction::class.java).compose(loadPlacesProcessor),
                        shared.ofType(AllPlacesGoneAction::class.java).compose(allPlacesGoneProcessor),
                        shared.ofType(ErrorDisplayedAction::class.java).compose(errorDisplayedProcessor)
                ))
            }
        }

val loadPlacesProcessor = ObservableTransformer { actions: Observable<LoadPlacesAction> ->
    actions.switchMap {
        placesRepository.getPlaces()
                .map { places -> MapResult.LoadPlacesResult.Success(places) }
                .cast(MapResult.LoadPlacesResult::class.java)
                .onErrorReturn { t -> MapResult.LoadPlacesResult.Failure(t) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(MapResult.LoadPlacesResult.InFlight)
    }
}

val mapReadyProcessor = OneToOneObservableTransformer<MapReadyAction, MapReadyResult>(MapReadyResult)

val errorDisplayedProcessor = OneToOneObservableTransformer<ErrorDisplayedAction, ErrorDisplayedResult>(ErrorDisplayedResult)

val allPlacesGoneProcessor = OneToOneObservableTransformer<AllPlacesGoneAction, AllPlacesGoneResult>(AllPlacesGoneResult)

class OneToOneObservableTransformer<Upstream, Downstream>(private val result: Downstream) : ObservableTransformer<Upstream, Downstream> {

    override fun apply(upstream: Observable<Upstream>): ObservableSource<Downstream> {
        return upstream.switchMap { Observable.just(result) }
    }
}

sealed class MapIntent {
    object MapReadyIntent: MapIntent()
    data class SearchPlacesIntent(val query: String): MapIntent()
    object AllPlacesGoneIntent: MapIntent()
    object ErrorDisplayedIntent: MapIntent()
}

sealed class MapAction {
    object MapReadyAction: MapAction()
    data class LoadPlacesAction(val query: String): MapAction()
    object AllPlacesGoneAction: MapAction()
    object ErrorDisplayedAction: MapAction()
}

sealed class MapResult {
    object MapReadyResult: MapResult()
    sealed class LoadPlacesResult: MapResult() {
        object InFlight: LoadPlacesResult()
        data class Failure(val throwable: Throwable): LoadPlacesResult()
        data class Success(val places: List<Place>): LoadPlacesResult()
    }
    object AllPlacesGoneResult: MapResult()
    object ErrorDisplayedResult: MapResult()
}

data class MapViewState(
        val reRender: Boolean,
        val isLoading: Boolean,
        val places: List<Place>,
        val placesTimestamp: Long?,
        val error: Throwable?
) {
    companion object Factory {
        fun default() = MapViewState(
                reRender = false,
                isLoading = false,
                places = emptyList(),
                placesTimestamp = null,
                error = null)
    }
}

val reducer = BiFunction {
    prevState: MapViewState, result: MapResult ->
    when(result) {
        is MapReadyResult -> prevState.copy(reRender = !prevState.reRender)
        is MapResult.LoadPlacesResult ->
            when(result) {
                is MapResult.LoadPlacesResult.InFlight ->
                    prevState.copy(isLoading = true, error = null)
                is MapResult.LoadPlacesResult.Success ->
                    prevState.copy(isLoading = false, places = result.places, placesTimestamp = System.currentTimeMillis())
                is MapResult.LoadPlacesResult.Failure ->
                    prevState.copy(isLoading = false, error = result.throwable)
            }
        is AllPlacesGoneResult -> prevState.copy(places = emptyList(), placesTimestamp = null)
        is ErrorDisplayedResult -> prevState.copy(error = null)
    }
}
