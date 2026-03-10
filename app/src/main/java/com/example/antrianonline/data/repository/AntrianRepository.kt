package com.example.antrianonline.data.repository

import com.example.antrianonline.data.api.ApiService
import com.example.antrianonline.data.model.*

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class AntrianRepository(private val api: ApiService) {

    // ── Auth ──────────────────────────────────────────────────────────────
    suspend fun register(namaLengkap: String, username: String, email: String,
                         noHp: String, password: String, passwordConfirm: String
    ): Result<LoginResponse> = safeCall {
        val body = mapOf(
            "nama_lengkap"          to namaLengkap,
            "username"              to username,
            "email"                 to email,
            "no_hp"                 to noHp,
            "password"              to password,
            "password_confirmation" to passwordConfirm,
        )
        val res = api.register(body)
        if (res.isSuccessful && res.body()?.success == true) {
            Result.Success(res.body()!!.data!!)
        } else {
            Result.Error(res.body()?.message ?: "Registrasi gagal")
        }
    }

    suspend fun login(username: String, password: String): Result<LoginResponse> = safeCall {
        val res = api.login(mapOf("username" to username, "password" to password))
        if (res.isSuccessful && res.body()?.success == true) {
            Result.Success(res.body()!!.data!!)
        } else {
            Result.Error(res.body()?.message ?: "Login gagal")
        }
    }

    suspend fun logout(): Result<Boolean> = safeCall {
        api.logout()
        Result.Success(true)
    }

    // ── Loket ─────────────────────────────────────────────────────────────
    suspend fun getLoketList(): Result<List<Loket>> = safeCall {
        val res = api.getLoketList()
        if (res.isSuccessful) Result.Success(res.body()?.data ?: emptyList())
        else Result.Error("Gagal memuat daftar loket")
    }

    // ── Antrian ───────────────────────────────────────────────────────────
    suspend fun ambilAntrian(idLoket: Int): Result<AntrianResponse> = safeCall {
        val res = api.ambilAntrian(mapOf("id_loket" to idLoket))
        if (res.isSuccessful && res.body()?.success == true) {
            Result.Success(res.body()!!.data!!)
        } else {
            Result.Error(res.body()?.message ?: "Gagal mengambil antrian")
        }
    }

    suspend fun getAntrianSaya(): Result<List<Antrian>> = safeCall {
        val res = api.getAntrianSaya()
        if (res.isSuccessful) Result.Success(res.body()?.data ?: emptyList())
        else Result.Error("Gagal memuat antrian")
    }

    suspend fun getMonitor(idLoket: Int): Result<MonitorData> = safeCall {
        val res = api.getMonitor(idLoket)
        if (res.isSuccessful) Result.Success(res.body()!!.data)
        else Result.Error("Gagal memuat monitor")
    }

    suspend fun batalAntrian(id: Int): Result<Boolean> = safeCall {
        val res = api.batalAntrian(id)
        if (res.isSuccessful) Result.Success(true)
        else Result.Error(res.body()?.message ?: "Gagal membatalkan antrian")
    }

    // ── Notifikasi ────────────────────────────────────────────────────────
    suspend fun getNotifikasi(): Result<NotifListResponse> = safeCall {
        val res = api.getNotifikasi()
        if (res.isSuccessful) Result.Success(res.body()!!)
        else Result.Error("Gagal memuat notifikasi")
    }

    suspend fun markReadAll(): Result<Boolean> = safeCall {
        api.readAll()
        Result.Success(true)
    }

    // ── Helper ────────────────────────────────────────────────────────────
    private suspend fun <T> safeCall(block: suspend () -> Result<T>): Result<T> {
        return try {
            block()
        } catch (e: Exception) {
            Result.Error("Koneksi gagal: ${e.localizedMessage}")
        }
    }
}
