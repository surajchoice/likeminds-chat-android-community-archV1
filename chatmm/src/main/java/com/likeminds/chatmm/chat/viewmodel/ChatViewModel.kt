package com.likeminds.chatmm.chat.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.likeminds.chatmm.SDKApplication.Companion.LOG_TAG
import com.likeminds.chatmm.dm.model.CheckDMTabViewData
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.likemindschat.LMChatClient
import javax.inject.Inject

class ChatViewModel @Inject constructor() : ViewModel() {

    private val _checkDMTabResponse = MutableLiveData<CheckDMTabViewData?>()
    val checkDMTabResponse: LiveData<CheckDMTabViewData?> = _checkDMTabResponse

    private val lmChatClient = LMChatClient.getInstance()

    //api to check whether dm is enabled or not
    fun checkDMTab() {
        viewModelScope.launchIO {
            val response = lmChatClient.checkDMTab()

            if (response.success) {
                val data = response.data
                val checkDMTabViewData = ViewDataConverter.convertCheckDMTabResponse(data)
                _checkDMTabResponse.postValue(checkDMTabViewData)
            } else {
                Log.d(LOG_TAG, "check dm tab failed: ${response.errorMessage}")
                _checkDMTabResponse.postValue(null)
            }
        }
    }
}