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

    private val _logoutResponse = MutableLiveData<Boolean>()
    val logoutResponse: LiveData<Boolean> = _logoutResponse

    fun initiateUser(
        context: Context,
        apiKey: String,
        userName: String,
        userId: String?,
        isGuest: Boolean
    ) {
        viewModelScope.launchIO {
            if (apiKey.isEmpty()) {
                _initiateErrorMessage.postValue(context.getString(R.string.empty_api_key))
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

            // save details to prefs
            saveDetailsToPrefs(
                apiKey,
                userUniqueId,
                memberId,
                uuid,
                name
            )

            // todo: member state

            //call register device api
            registerDevice()

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
}