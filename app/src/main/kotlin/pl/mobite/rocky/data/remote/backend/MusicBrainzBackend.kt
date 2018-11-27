package pl.mobite.rocky.data.remote.backend

import pl.mobite.rocky.data.remote.backend.responses.PlacesBackendResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface MusicBrainzBackend {

    @Throws(Throwable::class)
    @GET("place/?fmt=json")
    fun getPlaces(
        @Query("query") query: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): PlacesBackendResponse
}