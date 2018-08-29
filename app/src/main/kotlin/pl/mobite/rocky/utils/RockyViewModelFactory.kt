package pl.mobite.rocky.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import pl.mobite.rocky.data.remote.MusicBrainzService
import pl.mobite.rocky.data.remote.RetrofitProvider
import pl.mobite.rocky.data.remote.services.PlaceApiServiceImpl
import pl.mobite.rocky.data.repositories.place.PlaceRepositoryImpl
import pl.mobite.rocky.ui.map.MapViewModel


class RockyViewModelFactory private constructor() : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == MapViewModel::class.java) {
            val musicBrainzServiceProvider = { RetrofitProvider.instance.create(MusicBrainzService::class.java) }
            return MapViewModel(
                    PlaceRepositoryImpl(PlaceApiServiceImpl(musicBrainzServiceProvider)),
                    AndroidSchedulerProvider.instance) as T
        }
        throw IllegalStateException("Unknown view model class")
    }

    companion object {

        val instance = RockyViewModelFactory()
    }
}