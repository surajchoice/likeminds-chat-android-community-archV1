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
        private const val IS_GUEST = "IS_GUEST"
    }

    fun getUserUniqueId(): String {
        return getPreference(USER_UNIQUE_ID, "") ?: ""
    }

    fun saveUserUniqueId(memberId: String) {
        putPreference(USER_UNIQUE_ID, memberId)
    }

    fun setIsGuestUser(isGuest: Boolean?) {
        putPreference(IS_GUEST, isGuest ?: false)
    }

    fun getIsGuestUser(): Boolean {
        return getPreference(IS_GUEST, false)
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
            ?: ""
    }

    fun clearPrefs() {
        saveUserUniqueId("")
    }
}