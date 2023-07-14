package com.likeminds.chatmm.polls.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.polls.model.PollInfoData
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_POLL_RESULT_USER
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.conversation.model.GetConversationRequest
import com.likeminds.likemindschat.poll.model.GetPollUsersRequest
import javax.inject.Inject

class PollResultViewModel @Inject constructor() : ViewModel() {

    private val lmChatClient = LMChatClient.getInstance()

    // todo:
//    val memberStateData by lazy { MutableLiveData<MemberStateData?>() }

    val _memberList by lazy { MutableLiveData<List<BaseViewType>>() }
    val memberList: LiveData<List<BaseViewType>> = _memberList

    val _pollInfoData by lazy { MutableLiveData<PollInfoData?>() }
    val pollInfoData: LiveData<PollInfoData?> = _pollInfoData

    val chatroomId by lazy { MutableLiveData<String?>() }

    fun isAdmin(): Boolean {
        // TODO:
        return false
//        return MemberState.isAdmin(memberStateData.value?.state)
    }

    fun isMember(): Boolean {
        // TODO:
        return true
//        return MemberState.isMember(memberStateData.value?.state)
    }

    fun getPollInfoDataFromConversation(conversationId: String?) {
        viewModelScope.launchIO {
            if (conversationId == null) return@launchIO
            val getConversationRequest = GetConversationRequest.Builder()
                .conversationId(conversationId)
                .build()
            val conversation =
                lmChatClient.getConversation(getConversationRequest).data?.conversation
            _pollInfoData.postValue(
                ViewDataConverter.convertConversation(conversation)?.pollInfoData
            )
            chatroomId.postValue(conversation?.chatroomId.toString())
        }
    }

    // todo:
//    fun fetchMemberState() {
//        viewModelScope.launchIO {
//            when (val response = profileRepository.fetchMemberState()) {
//                is NetworkResponse.Error -> {
//                    memberStateData.postValue(null)
//                }
//
//                is NetworkResponse.Success -> {
//                    memberStateData.postValue(ViewDataConverter.convertMemberState(response.body.data))
//                }
//            }
//        }
//    }

    fun fetchPollParticipantsData(
        pollId: String?,
        communityId: String?,
        conversationId: String? = null,
    ) {
        if (pollId == null || communityId == null || conversationId == null)
            return

        fetchConversationPollParticipants(conversationId, pollId, communityId)
    }

    private fun fetchConversationPollParticipants(
        conversationId: String?,
        pollId: String?,
        communityId: String,
    ) {
        if (conversationId.isNullOrEmpty() || pollId.isNullOrEmpty()) return
        viewModelScope.launchIO {
            val getPollUsersRequest = GetPollUsersRequest.Builder()
                .pollId(pollId)
                .conversationId(conversationId)
                .build()
            val response = lmChatClient.getPollUsers(getPollUsersRequest)
            if (response.success) {
                val list = response.data?.members?.map {
                    ViewDataConverter.convertMember(it)
                        .toBuilder()
                        .dynamicViewType(ITEM_POLL_RESULT_USER)
                        .communityId(communityId)
                        .build()
                }
                _memberList.postValue(list)
            } else {
                _memberList.postValue(emptyList())
            }
        }
    }

    /**
     * Mix panel Events
     * */

    /**
     * Triggers when a user swipes between poll results
     **/
    fun sendPollResultsToggled(
        communityId: String?,
        communityName: String?,
        chatRoomId: String?,
        conversationId: String?,
        pollId: String?,
        pollText: String?,
    ) {
        LMAnalytics.track(
            LMAnalytics.Events.POLL_RESULTS_TOGGLED,
            mapOf(
                LMAnalytics.Keys.COMMUNITY_ID to communityId,
                LMAnalytics.Keys.COMMUNITY_NAME to communityName,
                LMAnalytics.Keys.CHATROOM_ID to chatRoomId,
                "poll_id" to pollId,
                "poll_text" to pollText,
                "conversation_id" to conversationId
            )
        )
    }
}