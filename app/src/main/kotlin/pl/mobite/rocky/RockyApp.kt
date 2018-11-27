package pl.mobite.rocky

import android.app.Application
import io.reactivex.plugins.RxJavaPlugins


open class RockyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this

        initRxJavaErrorHandler()
    }

    private fun initRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler { t: Throwable? ->
            if (t is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
            }
        }
    }

    companion object {

        @JvmStatic
        lateinit var instance: RockyApp
            private set
    }
}