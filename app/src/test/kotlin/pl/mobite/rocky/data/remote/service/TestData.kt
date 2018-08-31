package pl.mobite.rocky.data.remote.service

import pl.mobite.rocky.data.remote.models.PlaceApi
import pl.mobite.rocky.data.remote.models.PlaceApiResponse


const val dummyQuery = "query"

val dummyException = DummyException()

class DummyException: Throwable("dummy exception")

fun createDummyPlaceApiResponseList(count: Int, queryLimit: Int): List<PlaceApiResponse> {
    val pageNumbers = Math.ceil(count / queryLimit.toDouble()).toInt()
    return (0 until pageNumbers).map { pageNumber ->
        val firstId = pageNumber * queryLimit
        val placeApiCount = if (pageNumber < pageNumbers - 1) queryLimit else count - (pageNumber * queryLimit)
        PlaceApiResponse(null, count, getPageOffset(pageNumber, queryLimit), createDummyPlaceApiList(firstId, placeApiCount))
    }
}

fun createDummyPlaceApiList(firstId: Int, count: Int): List<PlaceApi> {
    return (0 until count).map { i -> createDummyPlaceAPI(firstId + i) }
}

fun createDummyPlaceAPI(id: Int) = PlaceApi(
        id.toString(),
        "Studio",
        "23",
        id * 10,
        "Studio name $id",
        "Studio address $id",
        null,
        null,
        null)

fun getPageOffset(pageNumber: Int, queryLimit: Int) = pageNumber * queryLimit

fun List<PlaceApiResponse>.getAllPlaceApiAsList(): List<PlaceApi> {
    val list = mutableListOf<PlaceApi>()
    forEach { placeAPiResponse ->
        placeAPiResponse.places?.let { list.addAll(it.filterNotNull()) }
    }
    return list
}