package pl.mobite.rocky.ui.map

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import pl.mobite.rocky.data.repositories.PlacesRepositoryImpl
import pl.mobite.rocky.utils.AndroidSchedulerProvider


class MapViewModel: ViewModel() {

    private val mapIntentsSource = PublishRelay.create<MapIntent>()

    private val mapViewStateSource = MapViewStateSourceFactory.create(
            mapIntentsSource,
            PlacesRepositoryImpl(),
            AndroidSchedulerProvider.instance
    )

    fun processIntents(intents: Observable<MapIntent>) {
        intents.subscribe(mapIntentsSource)
    }

    fun states(): Observable<MapViewState> {
        return mapViewStateSource
    }
}


