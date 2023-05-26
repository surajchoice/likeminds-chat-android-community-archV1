package com.likeminds.chatmm.utils

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import com.likeminds.chatmm.utils.sharedpreferences.BasePreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val application: Application
) : BasePreferences(USER_PREFS, application) {

    companion object {
        const val USER_PREFS = "user_prefs"
        const val USER_UNIQUE_ID = "user_unique_id"
    }

    fun getUserUniqueId(): String {
        return getPreference(USER_UNIQUE_ID, "") ?: ""
    }

    fun saveUserUniqueId(memberId: String) {
        putPreference(USER_UNIQUE_ID, memberId)
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
            ?: ""
    }
}