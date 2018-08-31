package pl.mobite.rocky.data.models

import java.io.Serializable


data class Place(val name: String, val openYear: Int, val cords: PlaceCords): Serializable

data class PlaceCords(val lat: Double, val lng: Double): Serializable
