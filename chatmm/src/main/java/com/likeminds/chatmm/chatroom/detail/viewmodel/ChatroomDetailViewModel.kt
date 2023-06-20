package com.likeminds.chatmm.chatroom.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.chatmm.chatroom.detail.model.*
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.conversation.model.LinkOGTagsViewData
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.ValueUtils.getEmailIfExist
import com.likeminds.chatmm.utils.ValueUtils.getUrlIfExist
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.chatmm.utils.membertagging.model.TagViewData
import com.likeminds.chatmm.utils.membertagging.util.MemberTaggingUtil
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.helper.model.DecodeUrlRequest
import com.likeminds.likemindschat.helper.model.DecodeUrlResponse
import com.likeminds.likemindschat.helper.model.GetTaggingListRequest
import com.likeminds.likemindschat.helper.model.GetTaggingListResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatroomDetailViewModel @Inject constructor(
    private val sdkPreferences: SDKPreferences
) : ViewModel() {

    companion object {
        const val CONVERSATIONS_LIMIT = 100
    }

    private val lmChatClient = LMChatClient.getInstance()

    //Contains all chatroom data, community data and more
    lateinit var chatroomDetail: ChatroomDetailData

    // todo: update this in initial data only
    /**
     * Returns the current member object
     */
    private var currentMemberFromDb: MemberViewData? = null

    /**
     * [taggingData] contains first -> page called
     * second -> Community Members and Groups
     */
    private val _taggingData = MutableLiveData<Pair<Int, ArrayList<TagViewData>>?>()
    val taggingData: LiveData<Pair<Int, ArrayList<TagViewData>>?> = _taggingData

    private val _linkOgTags: MutableLiveData<LinkOGTagsViewData?> = MutableLiveData()
    val linkOgTags: LiveData<LinkOGTagsViewData?> = _linkOgTags

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageFlow = errorEventChannel.receiveAsFlow()

    //Job for preview link API's calls
    private var previewLinkJob: Job? = null

    //Variable to hold current preview link, helps to avoid duplicate API calls
    private var previewLink: String? = null

    private fun getChatroom() = chatroomDetail.chatroom

    sealed class ErrorMessageEvent {
        data class GetTaggingList(val errorMessage: String?) : ErrorMessageEvent()
        data class DecodeUrl(val errorMessage: String?) : ErrorMessageEvent()
    }

    /**
     * @return
     * True is you are not admin in Announcement Room
     * False if you are admin in Announcement Room
     * False if it's not a Announcement Room
     */
    fun isNotAdminInAnnouncementRoom(): Boolean {
        return if (this::chatroomDetail.isInitialized &&
            chatroomDetail.chatroom?.type == TYPE_ANNOUNCEMENT
        ) {
            !(MemberState.isAdmin(currentMemberFromDb?.state))
        } else {
            false
        }
    }

    fun isDmChatroom(): Boolean {
        return getChatroom()?.type == TYPE_DIRECT_MESSAGE
    }

    fun isVoiceNoteSupportEnabled(): Boolean {
        return sdkPreferences.isVoiceNoteSupportEnabled()
    }

    fun getChatroomViewData(): ChatroomViewData? {
        return if (this::chatroomDetail.isInitialized) {
            chatroomDetail.chatroom
        } else {
            null
        }
    }

    fun getFirstConversationFromAdapter(items: List<BaseViewType>): ConversationViewData? {
        return items.firstOrNull {
            it is ConversationViewData
        } as? ConversationViewData
    }

    fun getLastConversationFromAdapter(items: List<BaseViewType>): ConversationViewData? {
        return items.lastOrNull {
            it is ConversationViewData
        } as? ConversationViewData
    }

    fun isAllBottomConversationsAdded(bottomConversation: ConversationViewData?): Boolean {
        return getConversationsBelowCount(bottomConversation) == 0
    }

    private fun getConversationsAboveCount(
        topConversation: ConversationViewData?,
    ): Int {
        if (topConversation == null) {
            return 0
        }
        // todo:
        return 1
//        return chatroomRepository.getConversationsAboveCount(
//            realm, getChatroom()!!.id().toInt(), topConversation
//        )
    }

    private fun getConversationsBelowCount(
        bottomConversation: ConversationViewData?,
    ): Int {
        if (bottomConversation == null) {
            return 0
        }
        // todo:
        return 1
//        return chatroomRepository.getConversationsBelowCount(
//            realm, chatroomId()?.toInt() ?: 0, bottomConversation
//        )
    }

    fun clearLinkPreview() {
        previewLinkJob?.cancel()
        previewLink = null
        _linkOgTags.postValue(null)
    }

    fun linkPreview(text: String) {
        if (text.isEmpty()) {
            clearLinkPreview()
            return
        }
        val emails = text.getEmailIfExist()
        if (!emails.isNullOrEmpty())
            return
        val link = text.getUrlIfExist()
        if (!link.isNullOrEmpty()) {
            if (previewLink == link) {
                return
            }
            previewLink = link
            previewLinkJob?.cancel()
            previewLinkJob = viewModelScope.launch {
                delay(250)
                decodeUrl(link)
            }
        } else {
            clearLinkPreview()
        }
    }

    fun decodeUrl(url: String) {
        viewModelScope.launchIO {
            val decodeUrlRequest = DecodeUrlRequest.Builder()
                .url(url)
                .build()

            val response = lmChatClient.decodeUrl(decodeUrlRequest)
            if (response.success) {
                decodeUrl(response.data)
            } else {
                errorEventChannel.send(ErrorMessageEvent.DecodeUrl(response.errorMessage))
            }
        }
    }

    private fun decodeUrl(decodeUrlResponse: DecodeUrlResponse?) {
        val ogTags = decodeUrlResponse?.ogTags ?: return
        _linkOgTags.postValue(ViewDataConverter.convertLinkOGTags(ogTags))
    }

    /**
     * Observes for users who can be tagged
     * live data pair of 2 list of members. First containing the community members and the second as chatroom participants
     */
    fun getMembersForTagging(
        chatroomId: String,
        page: Int,
        searchName: String?
    ) {
        viewModelScope.launchIO {
            val updatedSearchName = if (!searchName.isNullOrEmpty()) {
                searchName
            } else {
                null
            }
            val getTaggingListRequest = GetTaggingListRequest.Builder()
                .chatroomId(chatroomId)
                .page(page)
                .pageSize(MemberTaggingUtil.PAGE_SIZE)
                .searchName(updatedSearchName)
                .build()

            val response = lmChatClient.getTaggingList(getTaggingListRequest)
            if (response.success) {
                taggingResponseFetched(page, response.data)
            } else {
                errorEventChannel.send(ErrorMessageEvent.GetTaggingList(response.errorMessage))
            }
        }
    }

    private fun taggingResponseFetched(
        page: Int,
        data: GetTaggingListResponse?
    ) {
        //data from api
        val groupTags = data?.groupTags ?: emptyList()
        val chatroomParticipants = data?.chatroomParticipants ?: emptyList()
        val communityMembers = data?.communityMembers ?: emptyList()

        //send data to view
        _taggingData.postValue(
            Pair(
                page,
                MemberTaggingUtil.getTaggingData(
                    groupTags,
                    chatroomParticipants,
                    communityMembers
                )
            )
        )
    }
}