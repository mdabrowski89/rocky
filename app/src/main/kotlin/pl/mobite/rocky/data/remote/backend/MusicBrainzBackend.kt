package pl.mobite.rocky.data.remote.backend

import io.reactivex.Single
import pl.mobite.rocky.data.remote.backend.responses.PlacesBackendResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface MusicBrainzBackend {

    @GET("place/?fmt=json")
    fun getPlaces(
        @Query("query") query: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Single<PlacesBackendResponse>
}