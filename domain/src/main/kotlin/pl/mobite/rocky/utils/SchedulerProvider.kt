package pl.mobite.rocky.utils

import io.reactivex.Scheduler


interface SchedulerProvider {

    fun io(): Scheduler

    fun ui(): Scheduler
}