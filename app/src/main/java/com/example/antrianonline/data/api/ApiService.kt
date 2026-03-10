package com.example.antrianonline.data.api

import com.example.antrianonline.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────
    @POST("auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<ApiResponse<LoginResponse>>

    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<ApiResponse<LoginResponse>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Any>>

    @GET("auth/profile")
    suspend fun profile(): Response<ApiResponse<User>>

    // ── Loket ─────────────────────────────────────────────────────────────
    @GET("loket")
    suspend fun getLoketList(): Response<ApiResponse<List<Loket>>>

    @GET("loket/{id}")
    suspend fun getLoketDetail(@Path("id") id: Int): Response<ApiResponse<Loket>>

    // ── Antrian ───────────────────────────────────────────────────────────
    @POST("antrian/ambil")
    suspend fun ambilAntrian(@Body body: Map<String, Int>): Response<ApiResponse<AntrianResponse>>

    @GET("antrian/saya")
    suspend fun getAntrianSaya(): Response<ApiResponse<List<Antrian>>>

    @GET("antrian/monitor/{id_loket}")
    suspend fun getMonitor(@Path("id_loket") idLoket: Int): Response<MonitorResponse>

    @PATCH("antrian/{id}/batal")
    suspend fun batalAntrian(@Path("id") id: Int): Response<ApiResponse<Any>>

    // ── Notifikasi ────────────────────────────────────────────────────────
    @GET("notifikasi")
    suspend fun getNotifikasi(): Response<NotifListResponse>

    @PATCH("notifikasi/{id}/read")
    suspend fun markRead(@Path("id") id: Int): Response<ApiResponse<Any>>

    @PATCH("notifikasi/read-all")
    suspend fun readAll(): Response<ApiResponse<Any>>
}
