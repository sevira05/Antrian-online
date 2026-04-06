package com.example.antrianonline.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("antrian_session", Context.MODE_PRIVATE)

    companion object {
        // ================= LOGIN =================
        const val KEY_TOKEN     = "token"
        const val KEY_USER_ID   = "user_id"
        const val KEY_USERNAME  = "username"
        const val KEY_NAMA      = "nama"
        const val KEY_EMAIL     = "email"
        const val KEY_NO_HP     = "no_hp"

        // ================= ANTRIAN =================
        const val KEY_LOKET_PREFIX = "loket_"

        const val KEY_NO_ANTRIAN = "no_antrian"
        const val KEY_LOKET      = "loket"
        const val KEY_POSISI     = "posisi"
        const val KEY_ESTIMASI   = "estimasi"
        const val KEY_TANGGAL    = "tanggal"

        // ================= STATUS =================
        const val KEY_LAST_STATUS = "last_antrian_status"

        // ================= NOTIFIKASI =================
        const val KEY_NOTIF_LIST = "notif_list"
    }

    // ================= LOGIN =================
    fun saveSession(
        token: String,
        userId: Int,
        username: String,
        nama: String,
        email: String,
        noHp: String?
    ) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putString(KEY_NAMA, nama)
            .putString(KEY_EMAIL, email)
            .putString(KEY_NO_HP, noHp ?: "")
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getNama(): String? = prefs.getString(KEY_NAMA, null)
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getNoHp(): String? = prefs.getString(KEY_NO_HP, null)

    fun isLoggedIn(): Boolean = getToken() != null

    // ================= UPDATE PROFIL =================
    fun updateProfil(nama: String, username: String, email: String, noHp: String) {
        prefs.edit()
            .putString(KEY_NAMA, nama)
            .putString(KEY_USERNAME, username)
            .putString(KEY_EMAIL, email)
            .putString(KEY_NO_HP, noHp)
            .apply()
    }

    // ================= NOMOR ANTRIAN =================
    fun getNomorLoket(idLoket: Int): Int {
        return prefs.getInt("$KEY_LOKET_PREFIX$idLoket", 0)
    }

    fun saveNomorLoket(idLoket: Int, nomor: Int) {
        prefs.edit().putInt("$KEY_LOKET_PREFIX$idLoket", nomor).apply()
    }

    // ================= TIKET =================
    fun saveTiket(no: String, loket: String, posisi: Int, estimasi: Int, tanggal: String) {
        prefs.edit().apply {
            putString(KEY_NO_ANTRIAN, no)
            putString(KEY_LOKET, loket)
            putInt(KEY_POSISI, posisi)
            putInt(KEY_ESTIMASI, estimasi)
            putString(KEY_TANGGAL, tanggal)
            apply()
        }
    }

    fun getNoAntrian(): String = prefs.getString(KEY_NO_ANTRIAN, "-") ?: "-"
    fun getLoket(): String     = prefs.getString(KEY_LOKET, "-") ?: "-"
    fun getPosisi(): Int       = prefs.getInt(KEY_POSISI, 0)
    fun getEstimasi(): Int     = prefs.getInt(KEY_ESTIMASI, 0)
    fun getTanggal(): String   = prefs.getString(KEY_TANGGAL, "-") ?: "-"

    // ================= STATUS =================
    fun saveLastAntrianStatus(status: String) {
        prefs.edit().putString(KEY_LAST_STATUS, status).apply()
    }

    fun getLastAntrianStatus(): String? {
        return prefs.getString(KEY_LAST_STATUS, null)
    }

    // ================= 🔔 NOTIFIKASI =================
    fun saveNotif(judul: String, pesan: String) {
        val old = prefs.getString(KEY_NOTIF_LIST, "") ?: ""
        val newData = "$judul|$pesan|${System.currentTimeMillis()};$old"
        prefs.edit().putString(KEY_NOTIF_LIST, newData).apply()
    }

    fun getNotif(): List<Triple<String, String, String>> {
        val raw = prefs.getString(KEY_NOTIF_LIST, "") ?: ""
        if (raw.isEmpty()) return emptyList()

        return raw.split(";").mapNotNull {
            val parts = it.split("|")
            if (parts.size >= 3) Triple(parts[0], parts[1], parts[2])
            else null
        }
    }

    fun clearNotif() {
        prefs.edit().remove(KEY_NOTIF_LIST).apply()
    }

    // ================= CLEAR =================
    fun clear() {
        prefs.edit().clear().apply()
    }
}