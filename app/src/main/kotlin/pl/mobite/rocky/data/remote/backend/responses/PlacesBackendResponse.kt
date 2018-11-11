package pl.mobite.rocky.data.remote.backend.responses

import com.google.gson.annotations.SerializedName

data class PlacesBackendResponse(
        @SerializedName("created") val created: String?,
        @SerializedName("count") val count: Int?,
        @SerializedName("offset") val offset: Int?,
        @SerializedName("places") val placeResponses: List<PlaceBackendResponse?>?
)

data class PlaceBackendResponse(
        @SerializedName("id") val id: String?,
        @SerializedName("type") val type: String?,
        @SerializedName("type-id") val typeId: String?,
        @SerializedName("score") val score: Int?,
        @SerializedName("name") val name: String?,
        @SerializedName("address") val address: String?,
        @SerializedName("coordinates") val coordinatesResponse: CoordinatesBackendResponse?,
        @SerializedName("area") val area: AreaBackendResponse?,
        @SerializedName("life-span") val lifeSpan: LifeSpanBackendResponse?
)

data class CoordinatesBackendResponse(
        @SerializedName("latitude") val latitude: String?,
        @SerializedName("longitude") val longitude: String?
)

data class AreaBackendResponse(
        @SerializedName("id") val id: String?,
        @SerializedName("type") val type: String?,
        @SerializedName("type-id") val typeId: String?,
        @SerializedName("name") val name: String?,
        @SerializedName("sort-name") val sortName: String?,
        @SerializedName("life-span") val lifeSpan: LifeSpanBackendResponse?
)

data class LifeSpanBackendResponse(
        @SerializedName("begin") val begin: String?,
        @SerializedName("end") val end: String?,
        @SerializedName("ended") val ended: Boolean?
)