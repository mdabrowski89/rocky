package pl.mobite.rocky.ui.map

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.ui.map.MapAction.*
import pl.mobite.rocky.ui.map.MapResult.*
import pl.mobite.rocky.utils.SchedulerProvider
import pl.mobite.rocky.utils.SimpleActionProcessor

class MapActionProcessor(
        private val placeRepository: PlaceRepository,
        private val schedulerProvider: SchedulerProvider
) : ObservableTransformer<MapAction, MapResult> {

    override fun apply(actions: Observable<MapAction>): ObservableSource<MapResult> {
        return actions.publish { shared ->
            Observable.merge(listOf(
                    shared.ofType(ReRenderAction::class.java).compose(reRenderProcessor),
                    shared.ofType(LoadPlacesAction::class.java).compose(loadPlacesProcessor),
                    shared.ofType(ClearSearchResultsAction::class.java).compose(clearSearchResultsProcessor),
                    shared.ofType(ClearErrorAction::class.java).compose(clearErrorProcessor)
            ))
        }
    }

    private val loadPlacesProcessor = ObservableTransformer { actions: Observable<LoadPlacesAction> ->
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

    private val reRenderProcessor = SimpleActionProcessor<ReRenderAction, ReRenderResult>(ReRenderResult)

    private val clearErrorProcessor = SimpleActionProcessor<ClearErrorAction, ClearErrorResult>(ClearErrorResult)

    private val clearSearchResultsProcessor = SimpleActionProcessor<ClearSearchResultsAction, ClearSearchResultsResult>(ClearSearchResultsResult)

}