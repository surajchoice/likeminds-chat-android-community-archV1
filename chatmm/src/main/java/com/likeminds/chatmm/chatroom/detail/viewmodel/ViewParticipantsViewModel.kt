package com.likeminds.chatmm.chatroom.detail.viewmodel

import androidx.lifecycle.*
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.chatroom.model.GetParticipantsRequest
import com.likeminds.likemindschat.chatroom.model.GetParticipantsResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import kotlin.collections.set

class ViewParticipantsViewModel @Inject constructor() : ViewModel() {

    private val lmChatClient = LMChatClient.getInstance()

    companion object {
        const val PAGE_SIZE = 20
        const val PAGE_SIZE_KEY = "page_size"
        const val IS_SECRET_KEY = "is_secret"
        const val CHATROOM_ID_KEY = "chatroom_id"
        const val PAGE_KEY = "page"
        const val PARTICIPANT_NAME_KEY = "participant_name"
    }

    private val _fetchParticipantsResponse: MutableLiveData<Pair<List<MemberViewData>, Int>> =
        MutableLiveData()
    val fetchParticipantsResponse: LiveData<Pair<List<MemberViewData>, Int>> =
        _fetchParticipantsResponse

    private val errorMessageChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageEventFlow = errorMessageChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class GetParticipants(val errorMessage: String?) : ErrorMessageEvent()
    }

    fun fetchParticipants(
        isChatroomSecret: Boolean,
        chatroomId: String?,
        page: Int,
        participantName: String?
    ) {
        viewModelScope.launchIO {
            val queries = HashMap<String, Any?>()
            queries[IS_SECRET_KEY] = isChatroomSecret
            queries[CHATROOM_ID_KEY] = chatroomId
            queries[PAGE_KEY] = page
            queries[PAGE_SIZE_KEY] = PAGE_SIZE
            if (participantName != null) {
                queries[PARTICIPANT_NAME_KEY] = participantName
            }
            val updatedChatroomId = chatroomId ?: return@launchIO
            val getParticipantsRequest = GetParticipantsRequest.Builder()
                .chatroomId(updatedChatroomId)
                .isChatroomSecret(isChatroomSecret)
                .page(page)
                .pageSize(PAGE_SIZE)
                .participantName(participantName)
                .build()

            val response = lmChatClient.getParticipants(getParticipantsRequest)

            if (response.success) {
                participantsFetched(response.data)
            } else {
                errorMessageChannel.send(ErrorMessageEvent.GetParticipants(response.errorMessage))
            }
        }
    }

    private fun participantsFetched(data: GetParticipantsResponse?) {
        if (data == null) return
        val totalParticipants = data.totalParticipantsCount
        val listOfParticipants = data.participants

        val listOfMemberViewData = listOfParticipants.map {
            ViewDataConverter.convertParticipants(it)
        }
        _fetchParticipantsResponse.postValue(Pair(listOfMemberViewData, totalParticipants))
    }
}