package dev.almasum.health_connect.utils

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val PREFS_NAME = "MyPrefs"
    private const val INTERVAL_IN_MINUTES = "interval_in_minutes"
    private const val MODE = Context.MODE_PRIVATE

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var interval: Long
        get() = preferences.getLong(INTERVAL_IN_MINUTES, 30)
        set(value) = preferences.edit { it.putLong(INTERVAL_IN_MINUTES, value) }

}
