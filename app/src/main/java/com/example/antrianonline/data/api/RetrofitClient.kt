package com.example.antrianonline.data.api

import com.example.antrianonline.BuildConfig
import com.example.antrianonline.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var _api: ApiService? = null

    fun getApi(session: SessionManager): ApiService {
        if (_api == null) {
            _api = buildRetrofit(session).create(ApiService::class.java)
        }
        return _api!!
    }

    private fun buildRetrofit(session: SessionManager): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val token = session.getToken()
                val request = if (token != null) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .addHeader("Accept", "application/json")
                        .build()
                } else {
                    chain.request().newBuilder()
                        .addHeader("Accept", "application/json")
                        .build()
                }
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
