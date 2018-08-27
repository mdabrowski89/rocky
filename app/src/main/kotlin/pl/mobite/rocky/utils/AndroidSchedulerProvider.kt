package pl.mobite.rocky.utils

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers


class AndroidSchedulerProvider private constructor() : SchedulerProvider {

    override fun ui(): Scheduler = AndroidSchedulers.mainThread()

    companion object {

        val instance = AndroidSchedulerProvider()
    }
}