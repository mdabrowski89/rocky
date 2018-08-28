package pl.mobite.rocky.ui.map

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.schedulers.Schedulers
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.utils.OneToOneObservableTransformer
import pl.mobite.rocky.utils.SchedulerProvider



class MapActionProcessor(
        private val placesRepository: PlaceRepository,
        private val schedulerProvider: SchedulerProvider)
    : ObservableTransformer<MapAction, MapResult> {

    override fun apply(actions: Observable<MapAction>): ObservableSource<MapResult> {
        return actions.publish { shared ->
            Observable.merge(listOf(
                    shared.ofType(MapAction.MapReadyAction::class.java).compose(mapReadyProcessor),
                    shared.ofType(MapAction.LoadPlacesAction::class.java).compose(loadPlacesProcessor),
                    shared.ofType(MapAction.AllPlacesGoneAction::class.java).compose(allPlacesGoneProcessor),
                    shared.ofType(MapAction.ErrorDisplayedAction::class.java).compose(errorDisplayedProcessor)
            ))
        }
    }

    private val loadPlacesProcessor = ObservableTransformer { actions: Observable<MapAction.LoadPlacesAction> ->
        actions.switchMap { action ->
            placesRepository.getPlacesFrom1990(action.query)
                    .toObservable()
                    .map { places -> MapResult.LoadPlacesResult.Success(places) }
                    .cast(MapResult.LoadPlacesResult::class.java)
                    .onErrorReturn { t -> MapResult.LoadPlacesResult.Failure(t) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(schedulerProvider.ui())
                    .startWith(MapResult.LoadPlacesResult.InFlight)
        }
    }

    private val mapReadyProcessor = OneToOneObservableTransformer<MapAction.MapReadyAction, MapResult.MapReadyResult>(MapResult.MapReadyResult)

    private val errorDisplayedProcessor = OneToOneObservableTransformer<MapAction.ErrorDisplayedAction, MapResult.ErrorDisplayedResult>(MapResult.ErrorDisplayedResult)

    private val allPlacesGoneProcessor = OneToOneObservableTransformer<MapAction.AllPlacesGoneAction, MapResult.AllPlacesGoneResult>(MapResult.AllPlacesGoneResult)

}