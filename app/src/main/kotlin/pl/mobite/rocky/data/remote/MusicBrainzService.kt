package pl.mobite.rocky.data.remote

import io.reactivex.Single
import pl.mobite.rocky.data.remote.models.PlaceApiResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface MusicBrainzService {

    @GET("place/?fmt=json")
    fun getPlaces(
            @Query("query") query: String,
            @Query("offset") offset: Int = 0,
            @Query("limit") limit: Int = PAGE_LIMIT
    ): Single<PlaceApiResponse>

    companion object {

        const val PAGE_LIMIT = 20

        /* Due to server request limitations this is the maximum page number which can be fetched at once */
        const val MAX_API_PAGE = 14
    }
}