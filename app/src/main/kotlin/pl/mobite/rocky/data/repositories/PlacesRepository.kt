package pl.mobite.rocky.data.repositories

import io.reactivex.Observable
import pl.mobite.rocky.data.models.Place
import java.util.*
import java.util.concurrent.TimeUnit


class PlacesRepository {

    fun getPlaces(): Observable<List<Place>> {
        val random = Random()
        if (random.nextBoolean()) {
            return Observable.error(Throwable())
        } else {
            return Observable.just(listOf(
                    Place(1, "Place 1", 1995, 12.2, 15.3),
                    Place(1, "Place 2", 2010, 10.2, -1.3),
                    Place(1, "Place 3", 2005, -3.2, 8.3)
            )).delay(2, TimeUnit.SECONDS)
        }
    }
}