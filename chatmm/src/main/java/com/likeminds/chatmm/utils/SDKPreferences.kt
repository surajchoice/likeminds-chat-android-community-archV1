package com.likeminds.chatmm.utils

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import com.likeminds.chatmm.utils.sharedpreferences.BasePreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SDKPreferences @Inject constructor(
    private val application: Application
) : BasePreferences(SDK_PREFS, application) {

    companion object {
        const val SDK_PREFS = "sdk_prefs"
        const val USER_UNIQUE_ID = "user_unique_id"
        const val MEMBER_ID = "member_id"
        const val MICRO_POLLS_ENABLED = "MICRO_POLLS_ENABLED"
        const val GIF_SUPPORT_ENABLED = "GIF_SUPPORT_ENABLED"
        const val AUDIO_SUPPORT_ENABLED = "AUDIO_SUPPORT_ENABLED"
        const val VOICE_NOTE_ENABLED = "VOICE_NOTE_SUPPORT_ENABLED"
        const val HIDE_SECRET_CHATROOM_LOCK_ICON = "HIDE_SECRET_CHATROOM_LOCK_ICON"

        private const val API_KEY = "API_KEY"
        private const val IS_GUEST = "IS_GUEST"
    }

    fun setAPIKey(apiKey: String) {
        putPreference(API_KEY, apiKey)
    }

    fun getAPIKey(): String {
        return getPreference(API_KEY, "") ?: ""
    }

    fun getUserUniqueId(): String {
        return getPreference(USER_UNIQUE_ID, "") ?: ""
    }

    fun setUserUniqueId(userUniqueId: String) {
        putPreference(USER_UNIQUE_ID, userUniqueId)
    }

    fun getMemberId(): String {
        return getPreference(MEMBER_ID, "") ?: ""
    }

    fun setMemberId(memberId: String) {
        putPreference(MEMBER_ID, memberId)
    }

    fun setIsGuestUser(isGuest: Boolean?) {
        putPreference(IS_GUEST, isGuest ?: false)
    }

    fun getIsGuestUser(): Boolean {
        return getPreference(IS_GUEST, false)
    }

    fun setGifSupportEnabled(value: Boolean?) {
        putPreference(GIF_SUPPORT_ENABLED, value ?: false)
    }

    fun isGifSupportEnabled(): Boolean {
        return getPreference(GIF_SUPPORT_ENABLED, false)
    }

    fun setMicroPollsEnabled(value: Boolean?) {
        putPreference(MICRO_POLLS_ENABLED, value ?: false)
    }

    fun isMicroPollsEnabled(): Boolean {
        return getPreference(MICRO_POLLS_ENABLED, false)
    }

    fun setAudioSupportEnabled(value: Boolean?) {
        putPreference(AUDIO_SUPPORT_ENABLED, value ?: false)
    }

    fun isAudioSupportEnabled(): Boolean {
        return getPreference(AUDIO_SUPPORT_ENABLED, false)
    }

    fun setVoiceNoteSupportEnabled(value: Boolean?) {
        putPreference(VOICE_NOTE_ENABLED, value ?: false)
    }

    fun isVoiceNoteSupportEnabled(): Boolean {
        return getPreference(VOICE_NOTE_ENABLED, false)
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
            ?: ""
    }

    fun clearAuthPrefs() {
        setAPIKey("")
        setUserUniqueId("")
        setMemberId("")
        setIsGuestUser(false)
    }

    fun setDefaultConfigPrefs() {
        setMicroPollsEnabled(true)
        setGifSupportEnabled(true)
        setAudioSupportEnabled(true)
        setVoiceNoteSupportEnabled(true)
    }
}