package pl.mobite.rocky.data.remote.service

import pl.mobite.rocky.data.remote.models.PlaceApi
import pl.mobite.rocky.data.remote.models.PlaceApiResponse


const val testQuery = "test query"

val testException = TestException()

class TestException: Throwable("test exception")

fun getPageOffset(pageNumber: Int, queryLimit: Int) = pageNumber * queryLimit

fun createPlaceApiResponseList(count: Int, queryLimit: Int): List<PlaceApiResponse> {
    val pageNumbers = Math.ceil(count / queryLimit.toDouble()).toInt()
    return (0 until pageNumbers).map { pageNumber ->
        val firstId = pageNumber * queryLimit
        val placeApiCount = if (pageNumber < pageNumbers - 1) queryLimit else count - (pageNumber * queryLimit)
        PlaceApiResponse(null, count, getPageOffset(pageNumber, queryLimit), createPlaceApiList(firstId, placeApiCount))
    }
}

fun createPlaceApiList(firstId: Int, count: Int): List<PlaceApi> {
    return (0 until count).map { i -> createPlaceAPI(firstId + i) }
}

fun List<PlaceApiResponse>.getAllPlaceApiAsList(): List<PlaceApi> {
    val list = mutableListOf<PlaceApi>()
    forEach { placeAPiResponse ->
        placeAPiResponse.places?.let { list.addAll(it.filterNotNull()) }
    }
    return list
}

fun createPlaceAPI(id: Int) = PlaceApi(
        id.toString(),
        "Studio",
        "23",
        id * 10,
        "Studio name $id",
        "Studio address $id",
        null,
        null,
        null)