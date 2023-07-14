package com.likeminds.chatmm.polls.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.databinding.ItemCreatePollBinding
import com.likeminds.chatmm.polls.model.*
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.poll.model.PostPollConversationRequest
import com.likeminds.likemindschat.poll.model.PostPollConversationResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap
import kotlin.collections.arrayListOf
import kotlin.collections.filter
import kotlin.collections.forEach
import kotlin.collections.mapOf
import kotlin.collections.set

class CreatePollViewModel @Inject constructor() : ViewModel() {

    private val lmChatClient = LMChatClient.getInstance()

    private val _pollPosted: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    val pollPosted: LiveData<Boolean> = _pollPosted

    private val errorMessageChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageEventFlow = errorMessageChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class PostPollConversation(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val createPollItemBindingMap = LinkedHashMap<Int, ItemCreatePollBinding>()
    var endDate: Long? = null
        private set

    companion object {

        const val MULTIPLE_OPTION_STATE_MAX = "At max"
        const val MULTIPLE_OPTION_STATE_EXACTLY = "Exactly"
        const val MULTIPLE_OPTION_STATE_LEAST = "At least"
    }

    fun getPollOptionsSize() = createPollItemBindingMap.size

    fun setEndDate(endDate: Long) {
        this.endDate = endDate
    }

    fun createPollConversation(
        chatroom: ChatroomViewData,
        text: String,
        pollsViewData: ArrayList<PollViewData>,
        multipleSelectNoString: String?,
        multipleSelectStateString: String?,
        isLiveResults: Boolean,
        isAnonymousPoll: Boolean,
        isAddNewOption: Boolean,
    ) {
        viewModelScope.launchIO {
            val multipleSelectState = when (multipleSelectStateString) {
                MULTIPLE_OPTION_STATE_EXACTLY -> POLL_MULTIPLE_STATE_EXACTLY
                MULTIPLE_OPTION_STATE_LEAST -> POLL_MULTIPLE_STATE_LEAST
                MULTIPLE_OPTION_STATE_MAX -> POLL_MULTIPLE_STATE_MAX
                else -> null
            }
            val pollType = if (isLiveResults) {
                POLL_TYPE_INSTANT
            } else {
                POLL_TYPE_DEFERRED
            }

            val polls = ViewDataConverter.convertPolls(pollsViewData)

            val postPollConversationRequest = PostPollConversationRequest.Builder()
                .chatroomId(chatroom.id)
                .text(text)
                .polls(polls)
                .pollType(pollType)
                .expiryTime(endDate ?: 0L)
                .isAnonymous(isAnonymousPoll)
                .allowAddOption(isAddNewOption)
                .multipleSelectNo(multipleSelectNoString?.toInt())
                .multipleSelectState(multipleSelectState)
                .build()

            val response = lmChatClient.postPollConversation(postPollConversationRequest)

            if (response.success) {
                onPollConversationPosted(chatroom, response.data)
            } else {
                val errorMessage = response.errorMessage
                Log.e(
                    SDKApplication.LOG_TAG,
                    "poll creation failed: $errorMessage"
                )
                errorMessageChannel.send(ErrorMessageEvent.PostPollConversation(errorMessage))
            }
        }
    }

    private fun onPollConversationPosted(
        chatroom: ChatroomViewData,
        response: PostPollConversationResponse?,
    ) {
        val conversation = response?.conversation
        if (conversation != null) {
            _pollPosted.postValue(true)
            val conversationViewData = ViewDataConverter.convertConversation(conversation) ?: return
            sendPollCreationCompletedEvent(conversationViewData, chatroom)
        } else {
            _pollPosted.postValue(false)
        }
    }

    fun getInitialPollViewDataList(): ArrayList<BaseViewType> {
        createPollItemBindingMap.clear()
        return arrayListOf(getInitialPollViewData(), getInitialPollViewData())
    }

    fun getInitialPollViewData(): CreatePollViewData {
        return CreatePollViewData.Builder().build()
    }

    fun addItemCreatePollBinding(
        position: Int,
        itemCreatePollBinding: ItemCreatePollBinding,
    ): Int {
        createPollItemBindingMap[position] = itemCreatePollBinding
        return createPollItemBindingMap.size
    }

    fun removeItemCreatePollBinding(position: Int): Int {
        createPollItemBindingMap.remove(position)
        createPollItemBindingMap.keys.filter { it > position }.forEach {
            val oldItem = createPollItemBindingMap.remove(it)
            if (oldItem != null) {
                createPollItemBindingMap[it - 1] = oldItem
            }
        }
        return createPollItemBindingMap.size
    }

    fun getCreatePollItemBindingMap(): HashMap<Int, ItemCreatePollBinding> {
        return createPollItemBindingMap
    }

    fun getMultipleOptionStateList(): ArrayList<String> {
        return arrayListOf(
            MULTIPLE_OPTION_STATE_MAX,
            MULTIPLE_OPTION_STATE_EXACTLY,
            MULTIPLE_OPTION_STATE_LEAST
        )
    }

    fun getMultipleOptionNoList(): ArrayList<String> {
        return arrayListOf(
            "Select option",
            "1 option",
            "2 options",
            "3 options",
            "4 options",
            "5 options",
            "6 options",
            "7 options",
            "8 options",
            "9 options",
            "10 options"
        )
    }

    private fun sendPollCreationCompletedEvent(
        conversation: ConversationViewData,
        chatroom: ChatroomViewData,
    ) {
        LMAnalytics.track(
            LMAnalytics.Events.POLL_CREATION_COMPLETED,
            mapOf(
                LMAnalytics.Keys.CHATROOM_ID to chatroom.id,
                LMAnalytics.Keys.COMMUNITY_ID to chatroom.communityId,
                "chatroom_title" to chatroom.header,
                LMAnalytics.Keys.COMMUNITY_NAME to chatroom.communityName,
                "conversation_id" to conversation.id
            )
        )
    }

}
