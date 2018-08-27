package pl.mobite.rocky

import android.app.Application


class RockyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this
    }

    companion object {

        @JvmStatic lateinit var instance: RockyApp
            private set
    }
}