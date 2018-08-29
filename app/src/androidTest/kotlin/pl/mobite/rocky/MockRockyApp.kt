package pl.mobite.rocky

import com.squareup.rx2.idler.Rx2Idler
import io.reactivex.plugins.RxJavaPlugins


class MockRockyApp: RockyApp() {

    override fun onCreate() {
        RxJavaPlugins.setInitIoSchedulerHandler(Rx2Idler.create("RxJava 2.x I/O Scheduler"))
        super.onCreate()
    }
}