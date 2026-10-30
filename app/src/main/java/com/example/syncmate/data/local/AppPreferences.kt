package com.example.syncmate.data.local

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("subletsync_prefs", Context.MODE_PRIVATE)

    var isRememberDevice: Boolean
        get() = prefs.getBoolean("remember_device", false)
        set(value) = prefs.edit().putBoolean("remember_device", value).apply()

    var isLoggedIn: Boolean
        get() = prefs.getBoolean("is_logged_in", false)
        set(value) = prefs.edit().putBoolean("is_logged_in", value).apply()

    var userEmail: String?
        get() = prefs.getString("user_email", null)
        set(value) = prefs.edit().putString("user_email", value).apply()

    var authToken: String?
        get() = prefs.getString("auth_token", null)
        set(value) = prefs.edit().putString("auth_token", value).apply()

    var savedName: String?
        get() = prefs.getString("saved_name", null)
        set(value) = prefs.edit().putString("saved_name", value).apply()

    var savedUniversity: String?
        get() = prefs.getString("saved_university", null)
        set(value) = prefs.edit().putString("saved_university", value).apply()

    var savedBio: String?
        get() = prefs.getString("saved_bio", null)
        set(value) = prefs.edit().putString("saved_bio", value).apply()

    var savedPreferredLease: String?
        get() = prefs.getString("saved_preferred_lease", null)
        set(value) = prefs.edit().putString("saved_preferred_lease", value).apply()

    var savedMaxBudget: Double
        get() = prefs.getFloat("saved_max_budget", 15000.0f).toDouble()
        set(value) = prefs.edit().putFloat("saved_max_budget", value.toFloat()).apply()

    var savedPreferredLocation: String?
        get() = prefs.getString("saved_preferred_location", null)
        set(value) = prefs.edit().putString("saved_preferred_location", value).apply()

    var darkModeEnabled: Boolean
        get() = prefs.getBoolean("dark_mode", true)
        set(value) = prefs.edit().putBoolean("dark_mode", value).apply()

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean("notifications_enabled", true)
        set(value) = prefs.edit().putBoolean("notifications_enabled", value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }
}
