package pl.mobite.rocky.ui.map

import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.models.PlaceCords


const val testQuery = "test query"

val places = listOf(
        Place("Test place 1", 1995, PlaceCords(12.4, 15.3)),
        Place("Test place 2", 2000, PlaceCords(-10.1, 18.1)),
        Place("Test place 3", 2001, PlaceCords(15.7, 9.9))
)

val emptyPlaces = emptyList<Place>()

val testError = Throwable("test error")