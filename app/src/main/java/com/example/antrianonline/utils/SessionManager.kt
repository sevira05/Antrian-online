package com.example.antrianonline.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("antrian_session", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN         = "token"
        const val KEY_USER_ID       = "user_id"
        const val KEY_USERNAME      = "username"
        const val KEY_NAMA          = "nama"
        const val KEY_EMAIL         = "email"
        const val KEY_NO_HP         = "no_hp"
        const val KEY_LAST_STATUS   = "last_antrian_status"
    }

    fun saveSession(token: String, userId: Int, username: String, nama: String, email: String, noHp: String?) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putString(KEY_NAMA, nama)
            .putString(KEY_EMAIL, email)
            .putString(KEY_NO_HP, noHp ?: "")
            .apply()
    }

    fun getToken(): String?   = prefs.getString(KEY_TOKEN, null)
    fun getUserId(): Int       = prefs.getInt(KEY_USER_ID, -1)
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getNama(): String?     = prefs.getString(KEY_NAMA, null)
    fun getEmail(): String?    = prefs.getString(KEY_EMAIL, null)
    fun getNoHp(): String?     = prefs.getString(KEY_NO_HP, null)
    fun isLoggedIn(): Boolean  = getToken() != null

    fun saveLastAntrianStatus(status: String) =
        prefs.edit().putString(KEY_LAST_STATUS, status).apply()

    fun getLastAntrianStatus(): String? =
        prefs.getString(KEY_LAST_STATUS, null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}