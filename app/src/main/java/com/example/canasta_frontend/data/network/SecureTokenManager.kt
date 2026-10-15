package com.example.canasta_frontend.data.network

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureTokenManager(context: Context) {
    private val prefs: SharedPreferences = createEncryptedPrefs(context)

    private fun createEncryptedPrefs(context: Context): SharedPreferences {
        val fileName = "secure_canasta_prefs"
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                fileName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (_: Exception) {
            try {
                context.deleteSharedPreferences(fileName)
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                EncryptedSharedPreferences.create(
                    context,
                    fileName,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (_: Exception) {
                context.getSharedPreferences("canasta_prefs_fallback", Context.MODE_PRIVATE)
            }
        }
    }

    fun saveToken(token: String) {
        try {
            prefs.edit().putString("auth_token", token).apply()
        } catch (_: Exception) { }
    }

    fun getToken(): String? {
        return try {
            prefs.getString("auth_token", null)
        } catch (_: Exception) {
            null
        }
    }

    fun clearToken() {
        try {
            prefs.edit().remove("auth_token").apply()
        } catch (_: Exception) { }
    }
}
