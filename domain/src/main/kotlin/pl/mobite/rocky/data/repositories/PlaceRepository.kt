package pl.mobite.rocky.data.repositories

import io.reactivex.Observable
import pl.mobite.rocky.data.models.Place


interface PlaceRepository {

    fun getPlaces(): Observable<List<Place>>
}