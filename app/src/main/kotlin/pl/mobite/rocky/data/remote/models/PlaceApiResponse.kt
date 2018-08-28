package pl.mobite.rocky.data.remote.models

import com.google.gson.annotations.SerializedName

data class PlaceApiResponse(
        @SerializedName("created") val created: String?,
        @SerializedName("count") val count: Int?,
        @SerializedName("offset") val offset: Int?,
        @SerializedName("places") val places: List<PlaceApi?>?
)

data class PlaceApi(
        @SerializedName("id") val id: String?,
        @SerializedName("type") val type: String?,
        @SerializedName("type-id") val typeId: String?,
        @SerializedName("score") val score: Int?,
        @SerializedName("name") val name: String?,
        @SerializedName("address") val address: String?,
        @SerializedName("coordinates") val coordinates: CoordinatesApi?,
        @SerializedName("area") val area: AreaApi?,
        @SerializedName("life-span") val lifeSpan: LifeSpanApi?
)

data class CoordinatesApi(
        @SerializedName("latitude") val latitude: String?,
        @SerializedName("longitude") val longitude: String?
)

data class AreaApi(
        @SerializedName("id") val id: String?,
        @SerializedName("type") val type: String?,
        @SerializedName("type-id") val typeId: String?,
        @SerializedName("name") val name: String?,
        @SerializedName("sort-name") val sortName: String?,
        @SerializedName("life-span") val lifeSpan: LifeSpanApi?
)

data class LifeSpanApi(
        @SerializedName("begin") val begin: String?,
        @SerializedName("end") val end: String?,
        @SerializedName("ended") val ended: Boolean?
)