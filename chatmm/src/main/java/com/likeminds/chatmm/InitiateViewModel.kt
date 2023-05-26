package com.likeminds.chatmm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.initiateUser.model.InitiateUserRequest
import com.likeminds.likemindschat.initiateUser.model.InitiateUserResponse
import javax.inject.Inject

class InitiateViewModel @Inject constructor(
    private val sdkPreferences: SDKPreferences,
) : ViewModel() {

    private val lmChatClient = LMChatClient.getInstance()

    private val _initiateErrorMessage = MutableLiveData<String?>()
    val initiateErrorMessage: LiveData<String?> = _initiateErrorMessage

    private val _logoutResponse = MutableLiveData<Boolean>()
    val logoutResponse: LiveData<Boolean> = _logoutResponse

    fun initiateUser(
        context: Context,
        deviceId: String,
        userName: String,
        userId: String,
        isGuest: Boolean
    ) {
        viewModelScope.launchIO {
            val apiKey = sdkPreferences.getAPIKey()
            if (apiKey.isEmpty()) {
                _initiateErrorMessage.postValue(context.getString(R.string.empty_api_key))
                return@launchIO
            }

            //If user is guest take user unique id from local prefs
            val userUniqueId = if (isGuest) {
//                val userRO =
                ""
            } else {
                userId
            }

            val request = InitiateUserRequest.Builder()
                .apiKey(apiKey)
                .deviceId(deviceId)
                .userName(userName)
                .userId(userId)
                .isGuest(isGuest)
                .build()

            val initiateUserResponse = lmChatClient.initiateUser(request)
            if (initiateUserResponse.success) {
                val data = initiateUserResponse.data ?: return@launchIO
                handleInitiateResponse(data)
            } else {
                _initiateErrorMessage.postValue(initiateUserResponse.errorMessage)
            }
        }
    }

    private fun handleInitiateResponse(data: InitiateUserResponse) {
        if (data.logoutResponse != null) {
            //user is invalid
            _logoutResponse.postValue(true)
        } else {
            val user = data.user
            val id = user?.userUniqueId ?: ""

            // todo: save user id in preferences
        }
    }
}