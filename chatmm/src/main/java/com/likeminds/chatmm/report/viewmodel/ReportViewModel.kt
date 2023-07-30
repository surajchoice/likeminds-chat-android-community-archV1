package com.likeminds.chatmm.report.viewmodel

import androidx.lifecycle.*
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.report.model.ReportTagViewData
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.LMResponse
import com.likeminds.likemindschat.moderation.model.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class ReportViewModel @Inject constructor() : ViewModel() {

    private val lmChatClient = LMChatClient.getInstance()

    @Inject
    lateinit var userPreferences: UserPreferences

    private val _postReportResponse = MutableLiveData<Boolean>()
    val postReportResponse: LiveData<Boolean> = _postReportResponse

    private val _listOfTagViewData = MutableLiveData<List<ReportTagViewData>>()
    val listOfTagViewData: LiveData<List<ReportTagViewData>> = _listOfTagViewData

    private val errorMessageChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageEventFlow = errorMessageChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class PostReport(val errorMessage: String?) : ErrorMessageEvent()
        data class GetReportTags(val errorMessage: String?) : ErrorMessageEvent()
    }

    //for reporting member.conversation
    fun postReport(
        tagId: Int?,
        uuid: String?,
        reportedConversationId: String?,
        reason: String?
    ) {
        viewModelScope.launchIO {
            //if reason is empty then send [null] in request
            val updatedReason = if (reason.isNullOrEmpty()) null else reason

            //create request
            val request = PostReportRequest.Builder()
                .tagId(tagId ?: 0)
                .reason(updatedReason)
                .reportedConversationId(reportedConversationId)
                .uuid(uuid)
                .build()

            val response = lmChatClient.postReport(request)

            if (response.success) {
                _postReportResponse.postValue(true)
            } else {
                errorMessageChannel.send(ErrorMessageEvent.PostReport(response.errorMessage))
            }
        }
    }

    //Get report tags for reporting
    fun getReportTags(type: Int) {
        viewModelScope.launchIO {
            val request = GetReportTagsRequest.Builder()
                .type(type)
                .build()

            reportTagsFetched(lmChatClient.getReportTags(request))
        }
    }

    //to convert to TagViewData
    private fun reportTagsFetched(response: LMResponse<GetReportTagsResponse>) {
        viewModelScope.launchIO {
            if (response.success) {
                val data = response.data ?: return@launchIO
                val tags = data.tags
                val tagsViewData = ViewDataConverter.convertReportTag(tags)
                _listOfTagViewData.postValue(tagsViewData)
            } else {
                errorMessageChannel.send(ErrorMessageEvent.GetReportTags(response.errorMessage))
            }
        }
    }

    /**------------------------------------------------------------
     * Analytics
    ---------------------------------------------------------------*/

    /**
     * Triggers when a user clicks on the report member button from another user profile
     * @param uuid: uuid of the reported member
     * @param communityId: id of the community
     * */
    fun sendMemberProfileReport(
        uuid: String?,
        communityId: String?
    ) {
        LMAnalytics.track(
            LMAnalytics.Events.MEMBER_PROFILE_REPORT,
            mapOf(
                LMAnalytics.Keys.COMMUNITY_ID to communityId,
                "uuid" to uuid
            )
        )
    }

    /**
     * Triggers when a user selects a message and chooses ‘Report the message’ from the menu
     * @param conversationId: id of the message reported
     * @param issue: tag selected for the report
     * @param chatroomId: id of chatroom in which message was sent
     * @param communityId: id of community in which message was sent
     * @param chatroomName: name of the chatroom in which message was sent
     * @param conversationType: type of message, like: text, image, video etc.
     **/
    fun sendMessageReportedEvent(
        conversationId: String?,
        issue: String?,
        chatroomId: String?,
        communityId: String?,
        chatroomName: String?,
        conversationType: String?
    ) {
        val uuid = userPreferences.getUUID() //id of the user reporting the message
        LMAnalytics.track(
            LMAnalytics.Events.MESSAGE_REPORTED,
            mapOf(
                "conversation_id" to conversationId,
                LMAnalytics.Keys.COMMUNITY_ID to communityId,
                LMAnalytics.Keys.CHATROOM_ID to chatroomId,
                LMAnalytics.Keys.CHATROOM_NAME to chatroomName,
                "uuid" to uuid,
                "type" to conversationType,
                "issue" to issue
            )
        )
    }

    /**
     * Triggers when a user submits a report for a member
     * @param uuid: uuid of the member reported
     * @param issue: tag selected for report
     */
    fun sendMemberProfileReportConfirmed(
        communityId: String?,
        uuid: String?,
        issue: String?
    ) {
        LMAnalytics.track(
            LMAnalytics.Events.MEMBER_PROFILE_REPORT_CONFIRMED,
            mapOf(
                LMAnalytics.Keys.COMMUNITY_ID to communityId,
                "uuid" to uuid,
                "issue" to issue
            )
        )
    }
}