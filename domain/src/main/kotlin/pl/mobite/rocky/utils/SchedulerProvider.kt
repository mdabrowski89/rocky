package pl.mobite.rocky.utils

import io.reactivex.Scheduler


interface SchedulerProvider {

    fun ui(): Scheduler
}