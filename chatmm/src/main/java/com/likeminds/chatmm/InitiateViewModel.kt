package com.likeminds.chatmm

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.messaging.FirebaseMessaging
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.community.model.ConfigurationType
import com.likeminds.likemindschat.initiateUser.model.*
import javax.inject.Inject

class InitiateViewModel @Inject constructor(
    private val sdkPreferences: SDKPreferences,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val lmChatClient = LMChatClient.getInstance()

    private val _initiateErrorMessage = MutableLiveData<String?>()
    val initiateErrorMessage: LiveData<String?> = _initiateErrorMessage

    private val _initiateUserResponse = MutableLiveData<MemberViewData?>()
    val initiateUserResponse: LiveData<MemberViewData?> = _initiateUserResponse

    var isUserInitiated: Boolean = false

    private val _logoutResponse = MutableLiveData<Boolean>()
    val logoutResponse: LiveData<Boolean> = _logoutResponse

    companion object {
        const val WIDGET_MESSAGE_KEY = "message"
    }

    fun initiateUser(
        context: Context,
        apiKey: String,
        userName: String,
        userId: String?,
        isGuest: Boolean
    ) {
        viewModelScope.launchIO {
            if (apiKey.isEmpty()) {
                _initiateErrorMessage.postValue(context.getString(R.string.lm_chat_empty_api_key))
                return@launchIO
            }

            //If user is guest take user unique id from local prefs
            val uuid = if (isGuest) {
                val user = lmChatClient.getUser().data?.user
                user?.sdkClientInfo?.uuid
            } else {
                userId
            }

            val request = InitiateUserRequest.Builder()
                .apiKey(apiKey)
                .deviceId(userPreferences.getDeviceId())
                .userName(userName)
                .userId(uuid)
                .isGuest(isGuest)
                .build()

            val initiateUserResponse = lmChatClient.initiateUser(request)
            if (initiateUserResponse.success) {
                val data = initiateUserResponse.data ?: return@launchIO
                handleInitiateResponse(apiKey, data)
            } else {
                _initiateErrorMessage.postValue(initiateUserResponse.errorMessage)
            }
        }
    }

    private fun handleInitiateResponse(apiKey: String, data: InitiateUserResponse) {
        if (data.logoutResponse != null) {
            //user is invalid
            userPreferences.clearPrefs()
            _logoutResponse.postValue(true)
        } else {
            val user = data.user
            val userUniqueId = user?.userUniqueId ?: ""
            val memberId = user?.id.toString()
            val uuid = user?.sdkClientInfo?.uuid ?: ""
            val name = user?.name ?: ""

            isUserInitiated = true

            // save details to prefs
            saveDetailsToPrefs(
                apiKey,
                userUniqueId,
                memberId,
                uuid,
                name
            )

            //call register device api
            registerDevice()

            //call community configuration api
            getCommunityConfiguration()

            _initiateUserResponse.postValue(ViewDataConverter.convertUser(user))
        }
    }

    private fun saveDetailsToPrefs(
        apiKey: String,
        userUniqueId: String,
        memberId: String,
        uuid: String,
        name: String
    ) {
        sdkPreferences.setAPIKey(apiKey)
        userPreferences.apply {
            setIsGuestUser(false)
            setUserUniqueId(userUniqueId)
            setMemberId(memberId)
            setUUID(uuid)
            setMemberName(name)
        }
    }

    //call register device
    private fun registerDevice() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(
                        SDKApplication.LOG_TAG,
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@addOnCompleteListener
                }

                val token = task.result.toString()
                pushToken(token)
            }
        } catch (e: Exception) {
            Log.w(
                SDKApplication.LOG_TAG,
                "Please add firebase to your project to enable notifications"
            )
        }
    }

    private fun pushToken(token: String) {
        viewModelScope.launchIO {
            //create request
            val request = RegisterDeviceRequest.Builder()
                .deviceId(userPreferences.getDeviceId())
                .token(token)
                .build()

            //call api
            lmChatClient.registerDevice(request)
        }
    }

    fun getConfig() {
        viewModelScope.launchIO {
            val getConfigResponse = lmChatClient.getConfig()

            if (getConfigResponse.success) {
                val data = getConfigResponse.data
                if (data != null) {
                    sdkPreferences.setMicroPollsEnabled(data.enableMicroPolls)
                    sdkPreferences.setGifSupportEnabled(data.enableGifs)
                    sdkPreferences.setAudioSupportEnabled(data.enableAudio)
                    sdkPreferences.setVoiceNoteSupportEnabled(data.enableVoiceNote)
                }
            } else {
                Log.d(
                    SDKApplication.LOG_TAG,
                    "config api failed: ${getConfigResponse.errorMessage}"
                )
                // sets default values to config prefs
                sdkPreferences.setDefaultConfigPrefs()
            }
        }
    }

    //gets community configurations and save it into local db
    private fun getCommunityConfiguration() {
        viewModelScope.launchIO {
            val communityConfigurationResponse = lmChatClient.getCommunityConfigurations()
            if (communityConfigurationResponse.success) {
                val widgetConfiguration =
                    communityConfigurationResponse.data?.configurations?.find {
                        it.type == ConfigurationType.WIDGET_METADATA
                    } ?: return@launchIO

                val value = widgetConfiguration.value

                if (value.has(WIDGET_MESSAGE_KEY)) {
                    val isEnabled = value.getBoolean(WIDGET_MESSAGE_KEY)
                    sdkPreferences.setIsWidgetEnabled(isEnabled)
                } else {
                    sdkPreferences.setIsWidgetEnabled(false)
                }
            }
        }
    }
}