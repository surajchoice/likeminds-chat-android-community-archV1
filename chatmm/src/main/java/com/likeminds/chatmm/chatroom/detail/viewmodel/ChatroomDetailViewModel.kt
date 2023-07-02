package com.likeminds.chatmm.chatroom.detail.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.chatroom.detail.model.*
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.conversation.model.LinkOGTagsViewData
import com.likeminds.chatmm.media.MediaRepository
import com.likeminds.chatmm.media.model.MediaViewData
import com.likeminds.chatmm.utils.CommunityRightsUtil
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.ValueUtils.getEmailIfExist
import com.likeminds.chatmm.utils.ValueUtils.getUrlIfExist
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.chatmm.utils.mediauploader.worker.ConversationMediaUploadWorker
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.chatroom.model.FollowChatroomRequest
import com.likeminds.likemindschat.chatroom.model.LeaveSecretChatroomRequest
import com.likeminds.likemindschat.chatroom.model.MuteChatroomRequest
import com.likeminds.likemindschat.chatroom.model.SetChatroomTopicRequest
import com.likeminds.likemindschat.helper.model.DecodeUrlRequest
import com.likeminds.likemindschat.helper.model.DecodeUrlResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatroomDetailViewModel @Inject constructor(
    private val sdkPreferences: SDKPreferences,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    companion object {
        const val CONVERSATIONS_LIMIT = 100
    }

    private val lmChatClient = LMChatClient.getInstance()

    //Contains all chatroom data, community data and more
    lateinit var chatroomDetail: ChatroomDetailViewData

    // todo: update this in initial data only
    /**
     * Returns the current member object
     */
    private var currentMemberFromDb: MemberViewData? = null
    var currentMemberDataFromMemberState: MemberViewData? = null

    private val managementRights: ArrayList<ManagementRightPermissionViewData> = ArrayList()

    //default value is set to true, so that initially member can message,
    //but after api response check for the right to respond
    private val _canMemberRespond: MutableLiveData<Boolean> = MutableLiveData()
    val canMemberRespond: LiveData<Boolean> = _canMemberRespond

    private val _linkOgTags: MutableLiveData<LinkOGTagsViewData?> = MutableLiveData()
    val linkOgTags: LiveData<LinkOGTagsViewData?> = _linkOgTags

    private val _chatroomDetailLiveData by lazy { MutableLiveData<ChatroomDetailViewData?>() }
    val chatroomDetailLiveData: LiveData<ChatroomDetailViewData?> = _chatroomDetailLiveData

    //To set topic
    private val _setTopicResponse = MutableLiveData<ConversationViewData>()
    val setTopicResponse: LiveData<ConversationViewData> = _setTopicResponse

    private val _leaveSecretChatroomResponse = MutableLiveData<Boolean>()
    val leaveSecretChatroomResponse: LiveData<Boolean> = _leaveSecretChatroomResponse

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageFlow = errorEventChannel.receiveAsFlow()

    //Job for preview link API's calls
    private var previewLinkJob: Job? = null

    //Variable to hold current preview link, helps to avoid duplicate API calls
    private var previewLink: String? = null

    private fun getChatroom() = chatroomDetail.chatroom

    sealed class ErrorMessageEvent {
        data class DecodeUrl(val errorMessage: String?) : ErrorMessageEvent()
        data class FollowChatroom(val errorMessage: String?) : ErrorMessageEvent()
        data class MuteChatroom(val errorMessage: String?) : ErrorMessageEvent()
        data class SetChatroomTopic(val errorMessage: String?) : ErrorMessageEvent()
        data class LeaveSecretChatroom(val errorMessage: String?) : ErrorMessageEvent()
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

    fun isAudioSupportEnabled(): Boolean {
        return sdkPreferences.isAudioSupportEnabled()
    }

    fun isGifSupportEnabled(): Boolean {
        return sdkPreferences.isGifSupportEnabled()
    }

    // todo: member rights
    fun isMicroPollsEnabled(): Boolean {
        return sdkPreferences.isMicroPollsEnabled() /* && hasCreatePollRights() */
    }

    fun isAnnouncementChatroom(): Boolean {
        return ChatroomType.isAnnouncementRoom(chatroomDetail.chatroom?.type)
    }

    fun canMembersCanMessage(): Boolean? {
        return chatroomDetail.chatroom?.memberCanMessage
    }

    /**
     * Is the current member admin of the current chatroom
     */
    fun isAdminMember(): Boolean {
        return MemberState.isAdmin(currentMemberDataFromMemberState?.state)
    }

    fun hasMemberRespondRight(): Boolean {
        return _canMemberRespond.value == true
    }

    fun hasDeleteChatRoomRight(): Boolean {
        return CommunityRightsUtil.hasDeleteChatRoomRight(managementRights)
    }

    fun hasEditCommunityDetailRight(): Boolean {
        return CommunityRightsUtil.hasEditCommunityDetailRight(managementRights)
    }

    fun getChatroomViewData(): ChatroomViewData? {
        return if (this::chatroomDetail.isInitialized) {
            chatroomDetail.chatroom
        } else {
            null
        }
    }

    fun getCurrentTopic(): ConversationViewData? {
        return getChatroom()?.topic
    }

    private fun isChatroomCreator(): Boolean {
        return getChatroomViewData()?.memberViewData?.id == sdkPreferences.getMemberId()
    }

    /**
     * is current member can set a message as Chatroom Topic
     * Only allow when [User] is CM of the Community or he/she is the creator of chatroom
     **/
    fun canSetChatroomTopic(): Boolean {
        return isAdminMember() || isChatroomCreator()
    }

    fun getSlideUpVoiceNoteToast(): Boolean {
        return sdkPreferences.getSlideUpVoiceNoteToast()
    }

    fun setSlideUpVoiceNoteToast(value: Boolean) {
        sdkPreferences.setSlideUpVoiceNoteToast(value)
    }

    fun fetchUriDetails(
        context: Context,
        uris: List<Uri>,
        callback: (media: List<MediaViewData>) -> Unit,
    ) {
        mediaRepository.getLocalUrisDetails(context, uris, callback)
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

    fun getContentDownloadSettings(communityId: String) {
        // todo:
    }

    // follow/unfollow a chatroom
    fun followChatroom(
        chatroomId: String,
        value: Boolean,
        source: String
    ) {
        viewModelScope.launchIO {
            // create request
            val request = FollowChatroomRequest.Builder()
                .chatroomId(chatroomId)
                .memberId(sdkPreferences.getMemberId())
                .value(value)
                .build()

            // call api
            val response = lmChatClient.followChatroom(request)
            if (response.success) {
                // Update the ChatRoom actions once CM joins the chatroom
                if (value && getChatroom()?.isSecret == true) {
                    // todo: ask
//                    fetchChatroomFromNetwork()
                }
                if (value) {
                    sendChatroomFollowed(source)
                } else {
                    sendChatroomUnfollowed(source)
                }

                val oldChatroomViewData = chatroomDetail.chatroom
                if (oldChatroomViewData != null) {

                    //change follow status for chatroom
                    val followStatus = oldChatroomViewData.followStatus ?: return@launchIO
                    val newChatroomViewData = oldChatroomViewData.toBuilder()
                        .followStatus(!followStatus)
                        .build()

                    chatroomDetail =
                        chatroomDetail.toBuilder().chatroom(newChatroomViewData).build()
                    _chatroomDetailLiveData.postValue(chatroomDetail)
                }
            } else {
                // api failed send error message to ui
                val errorMessage = response.errorMessage
                Log.e(
                    SDKApplication.LOG_TAG,
                    "follow chatroom failed, $errorMessage"
                )
                errorEventChannel.send(ErrorMessageEvent.FollowChatroom(errorMessage))
            }
        }
    }

    // mute/un-mute a chatroom
    fun muteChatroom(
        chatroomId: String,
        value: Boolean
    ) {
        viewModelScope.launchIO {
            // create request
            val request = MuteChatroomRequest.Builder()
                .chatroomId(chatroomId.toInt())
                .value(value)
                .build()

            // call api
            val response = lmChatClient.muteChatroom(request)
            if (!response.success) {
                // api failed send error message to ui
                val errorMessage = response.errorMessage
                Log.e(
                    SDKApplication.LOG_TAG,
                    "mute chatroom failed, $errorMessage"
                )
                errorEventChannel.send(ErrorMessageEvent.MuteChatroom(errorMessage))
            }
        }
    }

    /** Set a conversation as current topic
     * @param chatroomId Id of the chatroom
     * @param conversation conversation object of the selected conversation
     */
    fun setChatroomTopic(
        chatroomId: String,
        conversation: ConversationViewData,
    ) {
        viewModelScope.launchIO {
            val request = SetChatroomTopicRequest.Builder()
                .chatroomId(chatroomId.toInt())
                .conversationId(conversation.id.toInt())
                .build()
            val response = lmChatClient.setChatroomTopic(request)
            if (response.success) {
                chatroomDetail = chatroomDetail.toBuilder()
                    .chatroom(
                        getChatroom()?.toBuilder()
                            ?.topic(conversation)
                            ?.build()
                    ).build()
                _setTopicResponse.postValue(conversation)
                sendSetChatroomTopicEvent(chatroomId, conversation.id, conversation.answer)
            } else {
                val errorMessage = response.errorMessage
                Log.e(SDKApplication.LOG_TAG, "set chatroom topic api failed: $errorMessage")
                errorEventChannel.send(ErrorMessageEvent.SetChatroomTopic(errorMessage))
            }
        }
    }

    fun leaveChatRoom(chatroomId: String) {
        viewModelScope.launchIO {
            val request = LeaveSecretChatroomRequest.Builder()
                .chatroomId(chatroomId.toInt())
                .isSecret(getChatroom()?.isSecret == true)
                .build()

            val response = lmChatClient.leaveSecretChatroom(request)
            if (response.success) {
                _leaveSecretChatroomResponse.postValue(true)
                sendChatRoomLeftEvent()
            } else {
                val errorMessage = response.errorMessage
                Log.e(SDKApplication.LOG_TAG, "secret leave failed: $errorMessage")
                errorEventChannel.send(ErrorMessageEvent.LeaveSecretChatroom(errorMessage))
            }
        }
    }

    fun deleteConversations(conversations: List<ConversationViewData>) {
        viewModelScope.launchIO {
            // todo
        }
    }

    fun updateChatroomWhileDeletingTopic() {
        chatroomDetail = chatroomDetail.toBuilder()
            .chatroom(
                getChatroom()?.toBuilder()
                    ?.topic(null)
                    ?.build()
            )
            .build()
    }

    fun updateChatRoomFollowStatus(value: Boolean) {
        val newChatroomViewData = getChatroom()?.toBuilder()
            ?.followStatus(value)
            ?.showFollowTelescope(!value)
            ?.build()
        chatroomDetail = chatroomDetail.toBuilder().chatroom(newChatroomViewData).build()
    }

    fun createRetryConversationMediaWorker(
        context: Context,
        conversationId: String,
        attachmentCount: Int,
    ) {
        if (conversationId.isEmpty() || attachmentCount <= 0) {
            return
        }
        val uploadData = uploadFilesViaWorker(context, conversationId, attachmentCount)
        // todo: update uuid
//        chatroomRepository.updateConversationUploadWorkerUUID(conversationId, uploadData.second)
        uploadData.first.enqueue()
    }

    @SuppressLint("CheckResult", "EnqueueWork", "RestrictedApi")
    private fun uploadFilesViaWorker(
        context: Context,
        conversationId: String,
        fileUriCount: Int,
    ): Pair<WorkContinuation, String> {
        val oneTimeWorkRequest =
            ConversationMediaUploadWorker.getInstance(conversationId, fileUriCount)
        val workContinuation = WorkManager.getInstance(context).beginWith(oneTimeWorkRequest)
        return Pair(workContinuation, oneTimeWorkRequest.id.toString())
    }

    fun resendFailedConversation(context: Context, conversation: ConversationViewData) {
        postFailedConversation(context, conversation)
    }

    private fun postFailedConversation(context: Context, conversation: ConversationViewData) {
        // todo:
    }

    fun deleteFailedConversation(conversationId: String) {
        // todo:
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

    /**------------------------------------------------------------
     * Analytics events
    ---------------------------------------------------------------*/

    /**
     * Triggers when the current user un-follows the current chatroom
     * @param source Source of the event
     */
    private fun sendChatroomUnfollowed(source: String) {
        // todo:
//        getChatroom()?.let { chatroomViewData ->
//            LMAnalytics.track(LMAnalytics.Keys.EVENT_CHAT_ROOM_UN_FOLLOWED, JSONObject().apply {
//                put("chatroom_id", chatroomViewData.id)
//                put("community_id", chatroomViewData.communityViewData?.id)
//                put("source", source)
//            })
//        }
    }

    /**
     * Triggers when the current user follows the current chatroom
     * @param source Source of the event
     */
    private fun sendChatroomFollowed(source: String) {
        getChatroom()?.let { chatroomViewData ->
//            LMAnalytics.track(LMAnalytics.Keys.EVENT_CHAT_ROOM_FOLLOWED, JSONObject().apply {
//                put("chatroom_id", chatroomViewData.id)
//                put("community_id", chatroomViewData.communityViewData?.id)
//                put("source", source)
//                put(
//                    "member_state", MemberState.getMemberState(
//                        chatroomViewData.memberState
//                    )
//                )
//            })
        }
    }

    /**
     * Triggers when the current chatroom is muted or un-muted
     * @param isChatroomMuted Chatroom is muted or not
     */
    fun sendChatroomMuted(isChatroomMuted: Boolean) {
//        LMAnalytics.track(
//            if (isChatroomMuted) {
//                LMAnalytics.Keys.CHATROOM_MUTED
//            } else {
//                LMAnalytics.Keys.CHATROOM_UNMUTED
//            }, JSONObject().apply {
//                put("chatroom_name", getChatroom()?.header)
//                put("community_id", getChatroom()?.id)
//            })
    }

    /**
     * Triggers when the user records a voice message
     **/
    fun sendVoiceNoteRecorded() {
        getChatroomViewData()?.let { chatroom ->
//            LMAnalytics.track(LMAnalytics.Keys.EVENT_VOICE_NOTE_RECORDED, JSONObject().apply {
//                put("chatroom_id", chatroom.id)
//                put("community_id", chatroom.communityId)
//                put("chatroom_type", chatroom.type)
//            })
        }
    }

    /**
     * Triggers when the user previews a voice message
     **/
    fun sendVoiceNotePreviewed() {
        getChatroomViewData()?.let { chatroom ->
//            LMAnalytics.track(LMAnalytics.Keys.EVENT_VOICE_NOTE_PREVIEWED, JSONObject().apply {
//                put("chatroom_id", chatroom.id)
//                put("community_id", chatroom.communityId)
//                put("chatroom_type", chatroom.type)
//            })
        }
    }

    /**
     * Triggers when the user removes a recorded voice message
     **/
    fun sendVoiceNoteCanceled() {
        getChatroomViewData()?.let { chatroom ->
//            LMAnalytics.track(LMAnalytics.Keys.EVENT_VOICE_NOTE_CANCELED, JSONObject().apply {
//                put("chatroom_id", chatroom.id)
//                put("community_id", chatroom.communityId)
//                put("chatroom_type", chatroom.type)
//            })
        }
    }

    /**
     * Triggers when the user sends a voice message
     **/
    private fun sendVoiceNoteSent(conversationId: String?) {
        if (conversationId.isNullOrEmpty()) return
        getChatroomViewData()?.let { chatroom ->
//            LMAnalytics.track(LMAnalytics.Keys.EVENT_VOICE_NOTE_SENT, JSONObject().apply {
//                put("chatroom_id", chatroom.id)
//                put("community_id", chatroom.communityId)
//                put("chatroom_type", chatroom.type)
//                put("message_id", conversationId)
//            })
        }
    }

    /**
     * Triggers when the user plays a voice message
     **/
    fun sendVoiceNotePlayed(conversationId: String) {
        getChatroomViewData()?.let { chatroom ->
//            LMAnalytics.track(LMAnalytics.Keys.EVENT_VOICE_NOTE_PLAYED, JSONObject().apply {
//                put("chatroom_id", chatroom.id)
//                put("community_id", chatroom.communityId)
//                put("chatroom_type", chatroom.type)
//                put("message_id", conversationId)
//            })
        }
    }

    /**
     * Triggers when the user plays the audio message
     **/
    fun sendAudioPlayedEvent(messageId: String) {
        getChatroomViewData()?.let { chatroom ->
//            LMAnalytics.track(
//                LMAnalytics.Keys.EVENT_AUDIO_PLAYED,
//                "chatroom_id" to chatroom.id,
//                "community_id" to chatroom.communityId,
//                "message_id" to messageId,
//            )
        }
    }

    /**
     * Triggers when the user clicks on a link
     **/
    fun sendChatLinkClickedEvent(messageId: String?, url: String) {
        getChatroomViewData()?.let { chatroom ->
//            LMAnalytics.track(
//                LMAnalytics.Keys.EVENT_CHAT_LINK_CLICKED,
//                "chatroom_id" to chatroom.id,
//                "community_id" to chatroom.communityId,
//                "user_id" to sdkPreferences.getMemberId(),
//                "message_id" to messageId,
//                "url" to url,
//                "type" to "link"
//            )
        }
    }

    /**
     * Triggers when the user copy messages
     **/
    fun sendMessageCopyEvent(message: String) {
        getChatroomViewData()?.let { chatroom ->
//            LMAnalytics.track(
//                LMAnalytics.Keys.EVENT_MESSAGE_COPIED,
//                "chatroom_id" to chatroom.id,
//                "community_id" to chatroom.communityId,
//                "messages" to message
//            )
        }
    }

    /**
     * Triggers when topic of chatroom is changed
     */
    private fun sendSetChatroomTopicEvent(
        chatroomId: String,
        conversationId: String,
        topic: String,
    ) {
//        LMAnalytics.track(LMAnalytics.Keys.EVENT_SET_CHATROOM_TOPIC, JSONObject().apply {
//            put("chatroom_id", chatroomId)
//            put("conversationId", conversationId)
//            put("topic", topic)
//        })
    }

    /**
     * Triggers when the current user leave the current chatroom
     */
    private fun sendChatRoomLeftEvent() {
//        LMAnalytics.track(LMAnalytics.Keys.EVENT_CHAT_ROOM_LEFT, JSONObject().apply {
//            put("community_id", chatroomDetail.chatroom?.communityId)
//            put("chatroom_id", chatroomDetail.chatroom?.id)
//            put("chatroom_name", chatroomDetail.chatroom?.header)
//            put("chatroom_type", chatroomDetail.chatroom?.getTypeName())
//            put("chatroom_category", "secret")
//        })
    }

    /**
     * Triggers when the current user starts sharing the chatroom
     */
    fun sendChatroomShared() {
//        LMAnalytics.track(LMAnalytics.Keys.EVENT_CHAT_ROOM_SHARED, JSONObject().apply {
//            put("chatroom_id", getChatroom()?.id)
//            put("chatroom_name", getChatroom()?.header)
//            put("community_id", getChatroom()?.communityId)
//            put("community_name", getChatroom()?.communityName)
//        })
    }
}