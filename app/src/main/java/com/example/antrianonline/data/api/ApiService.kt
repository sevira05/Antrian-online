package com.example.antrianonline.data.api

import com.example.antrianonline.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<BaseResponse>

    // ── Loket ─────────────────────────────────────────────────────────────────
    @GET("loket")
    suspend fun getLoket(@Header("Authorization") token: String): Response<LoketResponse>

    // ── Antrian ───────────────────────────────────────────────────────────────
    @GET("antrian/saya")
    suspend fun getAntrianSaya(@Header("Authorization") token: String): Response<AntrianResponse>

    @POST("antrian/ambil")
    suspend fun ambilAntrian(
        @Header("Authorization") token: String,
        @Body request: AmbilAntrianRequest
    ): Response<AntrianResponse>

    @POST("antrian/batal")
    suspend fun batalAntrian(@Header("Authorization") token: String): Response<BaseResponse>

    // ── Monitor ───────────────────────────────────────────────────────────────
    @GET("monitor/{id_loket}")
    suspend fun getMonitor(
        @Header("Authorization") token: String,
        @Path("id_loket") idLoket: Int
    ): Response<MonitorResponse>

    // ── Notifikasi ────────────────────────────────────────────────────────────
    @GET("notifikasi")
    suspend fun getNotifikasi(@Header("Authorization") token: String): Response<NotifikasiResponse>

    @POST("notifikasi/read-all")
    suspend fun readAllNotifikasi(@Header("Authorization") token: String): Response<BaseResponse>

    // ── Profil ────────────────────────────────────────────────────────────────
    @GET("profil")
    suspend fun getProfil(@Header("Authorization") token: String): Response<ProfilResponse>

    @PUT("profil")
    suspend fun updateProfil(
        @Header("Authorization") token: String,
        @Body request: UpdateProfilRequest
    ): Response<ProfilResponse>

    @PUT("profil/password")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body request: UpdatePasswordRequest
    ): Response<BaseResponse>

    // ── Ulasan ────────────────────────────────────────────────────────────────
    @POST("ulasan")
    suspend fun kirimUlasan(
        @Header("Authorization") token: String,
        @Body request: UlasanRequest
    ): Response<BaseResponse>

    @GET("ulasan/{id_loket}")
    suspend fun getUlasanLoket(
        @Header("Authorization") token: String,
        @Path("id_loket") idLoket: Int
    ): Response<UlasanListResponse>
}

// ── Response wrappers ─────────────────────────────────────────────────────────
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: User?
)

data class BaseResponse(val success: Boolean, val message: String)

data class LoketResponse(val success: Boolean, val data: List<Loket>)

data class AntrianResponse(val success: Boolean, val data: List<Antrian>)

data class MonitorResponse(val success: Boolean, val data: Antrian?)

data class NotifikasiResponse(
    val success: Boolean,
    val data: List<Notifikasi>,
    val unread: Int
)

data class ProfilResponse(val success: Boolean, val data: User?)

data class UlasanListResponse(
    val success: Boolean,
    val stats: RatingStats?,
    val data: List<UlasanItem>
)

// ── Request Bodies ────────────────────────────────────────────────────────────
data class RegisterRequest(
    val nama_lengkap: String, val username: String,
    val email: String, val no_hp: String,
    val password: String, val password_confirmation: String
)

data class LoginRequest(val username: String, val password: String)

data class AmbilAntrianRequest(val id_loket: Int)

data class UpdateProfilRequest(
    val nama_lengkap: String, val email: String, val no_hp: String
)

data class UpdatePasswordRequest(
    val password_lama: String,
    val password_baru: String,
    val password_baru_confirmation: String
)