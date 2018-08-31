package pl.mobite.rocky.ui.map

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import pl.mobite.rocky.data.repositories.PlaceRepository
import pl.mobite.rocky.utils.SchedulerProvider


class MapViewModel(
        placeRepository: PlaceRepository,
        schedulerProvider: SchedulerProvider,
        initialState: MapViewState? = null
) : ViewModel() {

    private val mapIntentsSource = PublishRelay.create<MapIntent>()

    private val mapViewStateSource: Observable<MapViewState> by lazy { MapViewStateSourceFactory.instance.create(
            mapIntentsSource,
            placeRepository,
            schedulerProvider,
            initialState
    )}

    fun processIntents(intents: Observable<MapIntent>) {
        intents.subscribe(mapIntentsSource)
    }

    fun states(): Observable<MapViewState> {
        return mapViewStateSource
    }
}


