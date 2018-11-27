package pl.mobite.rocky.ui.components.map

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.ui.components.map.MapAction.*
import pl.mobite.rocky.ui.components.map.processors.ClearSearchResultsProcessor
import pl.mobite.rocky.ui.components.map.processors.LoadPlacesProcessor
import pl.mobite.rocky.ui.components.map.processors.ReRenderProcessor
import pl.mobite.rocky.utils.SchedulerProvider

class MapActionProcessor(
    placeRepository: PlaceRepository,
    schedulerProvider: SchedulerProvider
): ObservableTransformer<MapAction, MapResult> {

    private val reRenderProcessor = ReRenderProcessor()
    private val loadPlacesProcessor = LoadPlacesProcessor(placeRepository, schedulerProvider)
    private val clearSearchResultsProcessor = ClearSearchResultsProcessor()

    override fun apply(actions: Observable<MapAction>): ObservableSource<MapResult> {
        return actions.publish { shared ->
            Observable.merge(
                listOf(
                    shared.ofType(ReRenderAction::class.java).compose(reRenderProcessor),
                    shared.ofType(LoadPlacesAction::class.java).compose(loadPlacesProcessor),
                    shared.ofType(ClearSearchResultsAction::class.java).compose(clearSearchResultsProcessor)
                )
            )
        }
    }
}