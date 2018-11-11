package pl.mobite.rocky.data.remote.backend

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.mobite.rocky.BuildConfig
import pl.mobite.rocky.R
import pl.mobite.rocky.RockyApp
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitProvider private constructor(){

    companion object {

        val instance: Retrofit by lazy {
            Retrofit.Builder()
                    .baseUrl(RockyApp.instance.getString(R.string.music_brainz_backend_url))
                    .client(createHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }

        private fun createHttpClient(): OkHttpClient {
            val builder = OkHttpClient.Builder()
            builder.addInterceptor(addUserAgentInterceptor())
            builder.addInterceptor(createHttpLoginInterceptor())
            return builder.build()
        }
    
        private fun createHttpLoginInterceptor(): Interceptor {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            return interceptor
        }
        
        private fun addUserAgentInterceptor() = Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("User-Agent", "Rocky/1.0.0 (mobite.app@gmail.com)")
                .method(original.method(), original.body())
                .build()
            chain.proceed(request)
        }
    }
}


