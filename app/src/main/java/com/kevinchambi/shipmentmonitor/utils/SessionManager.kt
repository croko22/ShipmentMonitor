package com.kevinchambi.shipmentmonitor.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("shipment_monitor_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
        private const val KEY_KEEP_SESSION = "keep_session"
        private const val KEY_EMAIL = "user_email"
    }

    fun saveToken(token: String, expiry: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_TOKEN_EXPIRY, expiry)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getTokenExpiry(): String? = prefs.getString(KEY_TOKEN_EXPIRY, null)

    fun setKeepSession(keep: Boolean) {
        prefs.edit().putBoolean(KEY_KEEP_SESSION, keep).apply()
    }

    fun shouldKeepSession(): Boolean = prefs.getBoolean(KEY_KEEP_SESSION, false)

    fun saveEmail(email: String) {
        prefs.edit().putString(KEY_EMAIL, email).apply()
    }

    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun isLoggedIn(): Boolean = getToken() != null

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
