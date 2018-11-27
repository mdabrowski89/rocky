package pl.mobite.rocky

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import pl.mobite.rocky.data.remote.backend.MusicBrainzBackend
import pl.mobite.rocky.data.remote.backend.RetrofitProvider
import pl.mobite.rocky.data.remote.repository.PlaceRemoteRepositoryImpl
import pl.mobite.rocky.data.repositories.PlaceRepositoryImpl
import pl.mobite.rocky.ui.components.map.MapViewModel
import pl.mobite.rocky.ui.components.map.MapViewState
import pl.mobite.rocky.utils.AndroidSchedulerProvider


class ViewModelFactory private constructor(private val args: Array<out Any?>): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == MapViewModel::class.java) {

            // TODO: pass this objects via DI
            val musicBrainzBackend by lazy { RetrofitProvider.instance.create(MusicBrainzBackend::class.java) }
            val placeRemoteRepo by lazy { PlaceRemoteRepositoryImpl(musicBrainzBackend) }
            val placeRepo by lazy { PlaceRepositoryImpl(placeRemoteRepo) }

            return MapViewModel(placeRepo, AndroidSchedulerProvider.instance, args[0] as MapViewState?) as T
        }
        throw IllegalStateException("Unknown view model class")
    }

    companion object {

        fun getInstance(vararg args: Any?) = ViewModelFactory(args)
    }
}