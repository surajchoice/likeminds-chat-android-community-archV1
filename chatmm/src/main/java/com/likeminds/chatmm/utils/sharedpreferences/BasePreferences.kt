package com.likeminds.chatmm.utils.sharedpreferences

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

abstract class BasePreferences protected constructor(
    prefName: String,
    application: Application
) {

    companion object {
        const val MASTER_PREF = "master_pref"
        const val ALL_PREFS_SET = "all_prefs_set"
    }

    init {
        val masterPref: SharedPreferences =
            application.getSharedPreferences(MASTER_PREF, Context.MODE_PRIVATE)
        val list = masterPref.getStringSet(ALL_PREFS_SET, emptySet())
        masterPref.edit().putStringSet(ALL_PREFS_SET, list!!.plus(prefName)).apply()
    }

    private val preferences: SharedPreferences =
        application.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    fun putPreference(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun putPreference(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun putPreference(key: String, value: Float) {
        preferences.edit().putFloat(key, value).apply()
    }

    fun putPreference(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    fun putPreference(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getPreference(key: String, defaultVal: Int): Int {
        return preferences.getInt(key, defaultVal)
    }

    fun getPreference(key: String, defaultVal: String): String? {
        return preferences.getString(key, defaultVal)
    }

    fun getPreference(key: String, defaultVal: Boolean): Boolean {
        return preferences.getBoolean(key, defaultVal)
    }

    fun getPreference(key: String, defaultVal: Float): Float {
        return preferences.getFloat(key, defaultVal)
    }

    fun getPreference(key: String, defaultVal: Long): Long {
        return preferences.getLong(key, defaultVal)
    }

    protected fun removePreference(key: String) {
        val editor = preferences.edit()
        editor.remove(key).apply()
    }

    fun clear() {
        // clear all the keys individually to notify the listeners
        val editor = preferences.edit()
        for (key in preferences.all.keys) {
            editor.remove(key)
        }
        editor.apply()
    }
}
