package pl.mobite.rocky.data.models


data class Place(val name: String, val openYear: Int, val cords: PlaceCords)

data class PlaceCords(val lat: Double, val lng: Double)
