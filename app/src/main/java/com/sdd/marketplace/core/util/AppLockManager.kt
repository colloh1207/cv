package com.sdd.marketplace.core.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLockManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sdd_security_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BIOMETRIC_LOCK = "biometric_lock_enabled"
        private const val KEY_PIN_LOCK = "pin_lock_enabled"
        private const val KEY_STORED_PIN = "stored_pin_hash"
    }

    fun isBiometricLockEnabled(): Boolean = prefs.getBoolean(KEY_BIOMETRIC_LOCK, false)

    fun setBiometricLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_LOCK, enabled).apply()
    }

    fun isPinLockEnabled(): Boolean = prefs.getBoolean(KEY_PIN_LOCK, false)

    fun setPinLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PIN_LOCK, enabled).apply()
    }

    fun savePin(pin: String) {
        val hash = pin.hashCode().toString()
        prefs.edit().putString(KEY_STORED_PIN, hash).apply()
    }

    fun verifyPin(pin: String): Boolean {
        val stored = prefs.getString(KEY_STORED_PIN, null) ?: return false
        return stored == pin.hashCode().toString()
    }

    fun hasPin(): Boolean = prefs.contains(KEY_STORED_PIN)

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
