package pl.mobite.rocky.ui.components.map.processors

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.ui.components.map.MapAction.LoadPlacesAction
import pl.mobite.rocky.ui.components.map.MapResult.LoadPlacesResult
import pl.mobite.rocky.utils.SchedulerProvider


class LoadPlacesProcessor(
    private val placeRepository: PlaceRepository,
    private val schedulerProvider: SchedulerProvider
): ObservableTransformer<LoadPlacesAction, LoadPlacesResult> {

    override fun apply(actions: Observable<LoadPlacesAction>): ObservableSource<LoadPlacesResult> {
        return actions.switchMap { action ->
            Observable
                .fromCallable {
                    placeRepository.getPlacesFrom1990(action.query)
                }
                .map { places -> LoadPlacesResult.Success(places, System.currentTimeMillis()) }
                .cast(LoadPlacesResult::class.java)
                .onErrorReturn { t -> LoadPlacesResult.Failure(t) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .startWith(LoadPlacesResult.InFlight)
        }
    }
}