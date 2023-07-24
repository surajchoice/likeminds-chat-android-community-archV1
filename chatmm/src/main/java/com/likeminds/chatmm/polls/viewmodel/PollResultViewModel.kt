package com.likeminds.chatmm.polls.viewmodel

import androidx.lifecycle.*
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.member.model.MemberState
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

    private val _memberState by lazy { MutableLiveData<Int?>() }
    val memberState: LiveData<Int?> = _memberState

    private val _memberList by lazy { MutableLiveData<List<BaseViewType>>() }
    val memberList: LiveData<List<BaseViewType>> = _memberList

    private val _pollInfoData by lazy { MutableLiveData<PollInfoData?>() }
    val pollInfoData: LiveData<PollInfoData?> = _pollInfoData

    val chatroomId by lazy { MutableLiveData<String?>() }

    fun isAdmin(): Boolean {
        return MemberState.isAdmin(_memberState.value)
    }

    fun isMember(): Boolean {
        return MemberState.isMember(_memberState.value)
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

    fun fetchMemberState() {
        viewModelScope.launchIO {
            val response = lmChatClient.getMemberState()
            if (response.success) {
                _memberState.postValue(response.data?.state)
            } else {
                _memberState.postValue(null)
            }
        }
    }

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