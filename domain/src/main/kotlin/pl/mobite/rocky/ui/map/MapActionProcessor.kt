package pl.mobite.rocky.ui.map

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.utils.OneToOneObservableTransformer
import pl.mobite.rocky.utils.SchedulerProvider

class MapActionProcessor(
        private val placeRepository: PlaceRepository,
        private val schedulerProvider: SchedulerProvider)
    : ObservableTransformer<MapAction, MapResult> {

    override fun apply(actions: Observable<MapAction>): ObservableSource<MapResult> {
        return actions.publish { shared ->
            Observable.merge(listOf(
                    shared.ofType(MapAction.ReRenderAction::class.java).compose(reRenderProcessor),
                    shared.ofType(MapAction.LoadPlacesAction::class.java).compose(loadPlacesProcessor),
                    shared.ofType(MapAction.ClearSearchResultsAction::class.java).compose(clearSearchResultsProcessor),
                    shared.ofType(MapAction.ClearErrorAction::class.java).compose(clearErrorProcessor)
            ))
        }
    }

    private val loadPlacesProcessor = ObservableTransformer { actions: Observable<MapAction.LoadPlacesAction> ->
        actions.switchMap { action ->
            placeRepository.getPlacesFrom1990(action.query)
                    .toObservable()
                    .map { places -> MapResult.LoadPlacesResult.Success(places, System.currentTimeMillis()) }
                    .cast(MapResult.LoadPlacesResult::class.java)
                    .onErrorReturn { t -> MapResult.LoadPlacesResult.Failure(t) }
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .startWith(MapResult.LoadPlacesResult.InFlight)
        }
    }

    private val reRenderProcessor = OneToOneObservableTransformer<MapAction.ReRenderAction, MapResult.ReRenderResult>(MapResult.ReRenderResult)

    private val clearErrorProcessor = OneToOneObservableTransformer<MapAction.ClearErrorAction, MapResult.ClearErrorResult>(MapResult.ClearErrorResult)

    private val clearSearchResultsProcessor = OneToOneObservableTransformer<MapAction.ClearSearchResultsAction, MapResult.ClearSearchResultsResult>(MapResult.ClearSearchResultsResult)

}