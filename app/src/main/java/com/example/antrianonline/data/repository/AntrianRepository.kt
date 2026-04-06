package com.example.antrianonline.data.repository

import com.example.antrianonline.data.api.AmbilAntrianRequest
import com.example.antrianonline.data.api.ApiService
import com.example.antrianonline.data.api.LoginRequest
import com.example.antrianonline.data.api.RegisterRequest
import com.example.antrianonline.data.api.UpdatePasswordRequest
import com.example.antrianonline.data.api.UpdateProfilRequest
import com.example.antrianonline.data.model.UlasanRequest
import com.example.antrianonline.utils.SessionManager
import org.json.JSONObject
import retrofit2.Response

// ── Sealed Result ─────────────────────────────────────────────────────────────
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// ── Repository ────────────────────────────────────────────────────────────────
class AntrianRepository(
    private val api: ApiService,
    private val session: SessionManager
) {
    private val token get() = "Bearer ${session.getToken()}"

    // Auth
    suspend fun login(username: String, password: String) =
        safeCall { api.login(LoginRequest(username = username, password = password)) }

    suspend fun register(
        nama: String, username: String, email: String,
        noHp: String, password: String, confirm: String
    ) = safeCall {
        api.register(
            RegisterRequest(
                nama_lengkap          = nama,
                username              = username,
                email                 = email,
                no_hp                 = noHp,
                password              = password,
                password_confirmation = confirm
            )
        )
    }

    suspend fun logout() = safeCall { api.logout(token) }

    // Loket
    suspend fun getLoket()     = safeCall { api.getLoket(token) }
    suspend fun getLoketList() = safeCall { api.getLoket(token) }

    // Antrian
    suspend fun getAntrianSaya()              = safeCall { api.getAntrianSaya(token) }
    suspend fun ambilAntrian(idLoket: Int)    = safeCall { api.ambilAntrian(token, AmbilAntrianRequest(idLoket)) }
    suspend fun batalAntrian()                = safeCall { api.batalAntrian(token) }

    // Monitor
    suspend fun getMonitor(idLoket: Int)      = safeCall { api.getMonitor(token, idLoket) }

    // Notifikasi
    suspend fun getNotifikasi()               = safeCall { api.getNotifikasi(token) }
    suspend fun markReadAll()                 = safeCall { api.readAllNotifikasi(token) }

    // Profil
    suspend fun getProfil()                   = safeCall { api.getProfil(token) }
    suspend fun updateProfil(nama: String, email: String, noHp: String) =
        safeCall { api.updateProfil(token, UpdateProfilRequest(nama, email, noHp)) }
    suspend fun updatePassword(lama: String, baru: String, confirm: String) =
        safeCall { api.updatePassword(token, UpdatePasswordRequest(lama, baru, confirm)) }

    // Ulasan
    suspend fun kirimUlasan(idLoket: Int, idRiwayat: Int, rating: Int, komentar: String?) =
        safeCall {
            api.kirimUlasan(token, UlasanRequest(
                idLoket   = idLoket,
                idRiwayat = idRiwayat,
                rating    = rating,
                komentar  = komentar
            ))
        }
    suspend fun getUlasanLoket(idLoket: Int)  = safeCall { api.getUlasanLoket(token, idLoket) }

    // Helper
    private suspend fun <T> safeCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val resp = call()
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) Result.Success(body)
                else Result.Error("Response kosong.")
            } else {
                val msg = resp.errorBody()?.string()
                    ?.let { runCatching { JSONObject(it).optString("message", it) }.getOrNull() }
                    ?: "Error ${resp.code()}"
                Result.Error(msg)
            }
        } catch (e: Exception) {
            Result.Error("Koneksi bermasalah: ${e.localizedMessage}")
        }
    }
}