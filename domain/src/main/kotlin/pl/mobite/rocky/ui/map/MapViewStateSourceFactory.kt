package pl.mobite.rocky.ui.map

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.utils.SchedulerProvider


class MapViewStateSourceFactory private constructor(){

    companion object {

        fun create(
                intentsSource: PublishRelay<MapIntent>,
                placeRepository: PlaceRepository,
                schedulerProvider: SchedulerProvider)
                : Observable<MapViewState> {
            val mapIntentInterpreter = MapIntentInterpreter()
            val mapActionProcessor = MapActionProcessor(placeRepository, schedulerProvider)
            val mapReducer = MapReducer()
            return intentsSource
                    .map(mapIntentInterpreter)
                    .compose(mapActionProcessor)
                    .scan(MapViewState.default(), mapReducer)
                    .distinctUntilChanged()
                    .replay(1)
                    .autoConnect(0)
        }
    }
}