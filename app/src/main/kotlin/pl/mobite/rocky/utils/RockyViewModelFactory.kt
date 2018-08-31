package pl.mobite.rocky.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import pl.mobite.rocky.data.remote.MusicBrainzService
import pl.mobite.rocky.data.remote.RetrofitProvider
import pl.mobite.rocky.data.remote.services.PlaceApiServiceImpl
import pl.mobite.rocky.data.repositories.place.PlaceRepositoryImpl
import pl.mobite.rocky.ui.map.MapViewModel
import pl.mobite.rocky.ui.map.MapViewState


class RockyViewModelFactory private constructor(private val args: Array<out Any?>) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == MapViewModel::class.java) {
            val musicBrainzServiceProvider = { RetrofitProvider.instance.create(MusicBrainzService::class.java) }
            return MapViewModel(
                    PlaceRepositoryImpl(PlaceApiServiceImpl(musicBrainzServiceProvider)),
                    AndroidSchedulerProvider.instance,
                    args[0] as MapViewState?) as T
        }
        throw IllegalStateException("Unknown view model class")
    }

    companion object {

        fun getInstance(vararg args: Any?) = RockyViewModelFactory(args)
    }
}