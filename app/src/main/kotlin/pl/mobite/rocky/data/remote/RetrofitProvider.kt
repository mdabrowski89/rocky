package pl.mobite.rocky.data.remote

import okhttp3.OkHttpClient
import pl.mobite.rocky.R
import pl.mobite.rocky.RockyApp
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


val retrofit: Retrofit by lazy {
    Retrofit.Builder()
            .baseUrl(RockyApp.instance.getString(R.string.music_brainz_api_url))
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
}

fun createHttpClient(): OkHttpClient {
    val builder = OkHttpClient.Builder()
    builder.addInterceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
                .header("User-Agent", "Rocky/1.0.0 (mobite.app@gmail.com)")
                .method(original.method(), original.body())
                .build()
        chain.proceed(request)
    }
    return builder.build()
}
