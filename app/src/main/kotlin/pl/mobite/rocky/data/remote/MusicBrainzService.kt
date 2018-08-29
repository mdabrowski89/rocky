package pl.mobite.rocky.data.remote

import io.reactivex.Single
import pl.mobite.rocky.data.remote.models.PlaceApiResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface MusicBrainzService {

    @GET("place/?fmt=json")
    fun getPlaces(
            @Query("query") query: String,
            @Query("offset") offset: Int,
            @Query("limit") limit: Int
    ): Single<PlaceApiResponse>
}