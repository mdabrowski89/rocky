package pl.mobite.rocky.ui.map

import pl.mobite.rocky.data.models.Place


const val dummyQuery = "query"

val dummyPlaces = listOf(
        Place("Test place 1", 1995, 12.4, 15.3),
        Place("Test place 2", 2000, -10.1, 18.1),
        Place("Test place 3", 2001, 15.7, 9.9)
)

val dummyEmptyPlaces = emptyList<Place>()

val dummyException = Throwable("dummy error")
