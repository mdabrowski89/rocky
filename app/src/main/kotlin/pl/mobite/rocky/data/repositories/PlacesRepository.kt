package pl.mobite.rocky.data.repositories

import io.reactivex.Observable
import pl.mobite.rocky.data.models.Place
import java.util.concurrent.TimeUnit


class PlacesRepository {

    fun getPlaces(): Observable<List<Place>> {
        return Observable.just(listOf(
                Place(1, "random", 12.2, 15.3)
        )).delay(3, TimeUnit.SECONDS)
    }
}