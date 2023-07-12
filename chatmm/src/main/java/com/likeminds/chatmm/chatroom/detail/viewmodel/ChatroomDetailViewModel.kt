package com.likeminds.chatmm.chatroom.detail.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.*
import androidx.work.WorkContinuation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.chatroom.detail.model.*
import com.likeminds.chatmm.chatroom.detail.util.ChatroomUtil
import com.likeminds.chatmm.chatroom.detail.util.ChatroomUtil.getTypeName
import com.likeminds.chatmm.conversation.model.*
import com.likeminds.chatmm.media.MediaRepository
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.member.model.MemberState
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.utils.*
import com.likeminds.chatmm.utils.ValueUtils.getEmailIfExist
import com.likeminds.chatmm.utils.ValueUtils.getUrlIfExist
import com.likeminds.chatmm.utils.coroutine.launchDefault
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.chatmm.utils.coroutine.launchMain
import com.likeminds.chatmm.utils.file.util.FileUtil
import com.likeminds.chatmm.utils.mediauploader.model.GenericFileRequest
import com.likeminds.chatmm.utils.mediauploader.worker.ConversationMediaUploadWorker
import com.likeminds.chatmm.utils.mediauploader.worker.UploadHelper
import com.likeminds.chatmm.utils.membertagging.model.TagViewData
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.chatroom.model.*
import com.likeminds.likemindschat.community.model.GetMemberRequest
import com.likeminds.likemindschat.conversation.model.*
import com.likeminds.likemindschat.conversation.util.ConversationChangeListener
import com.likeminds.likemindschat.conversation.util.GetConversationCountType
import com.likeminds.likemindschat.conversation.util.GetConversationType
import com.likeminds.likemindschat.conversation.util.LoadConversationType
import com.likeminds.likemindschat.helper.model.DecodeUrlRequest
import com.likeminds.likemindschat.helper.model.DecodeUrlResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class ChatroomDetailViewModel @Inject constructor(
    private val sdkPreferences: SDKPreferences,
    private val userPreferences: UserPreferences,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ChatroomDetail"
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

    //to check whether chatroom is loaded for first time when opened through explore feed/deeplinks
    private var chatroomWasNotLoaded = false

    private var isFirstSyncOngoing = false

    private val managementRights: ArrayList<ManagementRightPermissionViewData> by lazy { ArrayList() }

    //default value is set to true, so that initially member can message,
    //but after api response check for the right to respond
    private val _canMemberRespond: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    val canMemberRespond: LiveData<Boolean> = _canMemberRespond

    private val _canMemberCreatePoll: MutableLiveData<Boolean> by lazy { MutableLiveData(true) }
    val canMemberCreatePoll: LiveData<Boolean> = _canMemberCreatePoll

    private val _linkOgTags: MutableLiveData<LinkOGTagsViewData?> by lazy { MutableLiveData() }
    val linkOgTags: LiveData<LinkOGTagsViewData?> = _linkOgTags

    private val _chatroomDetailLiveData by lazy { MutableLiveData<ChatroomDetailViewData?>() }
    val chatroomDetailLiveData: LiveData<ChatroomDetailViewData?> = _chatroomDetailLiveData

    //Chatroom, Data, Scroll position
    private val _initialData by lazy { MutableLiveData<InitialViewData?>() }
    val initialData: LiveData<InitialViewData?> = _initialData

    private val _paginatedData by lazy { MutableLiveData<PaginatedViewData>() }
    val paginatedData: LiveData<PaginatedViewData> = _paginatedData

    //Data, Scroll state
    private val _scrolledData by lazy { MutableLiveData<PaginatedViewData>() }
    val scrolledData: LiveData<PaginatedViewData> = _scrolledData


    //Data, Scroll state
//    private val scrolledData by lazy { MutableLiveData<PaginatedData>() }

    //To set topic
    private val _setTopicResponse by lazy { MutableLiveData<ConversationViewData>() }
    val setTopicResponse: LiveData<ConversationViewData> = _setTopicResponse

    private val _contentDownloadSettingsLiveData: MutableLiveData<List<String>?> by lazy {
        MutableLiveData<List<String>?>()
    }

    val contentDownloadSettingsLiveData: LiveData<List<String>?> = _contentDownloadSettingsLiveData

    private val _leaveSecretChatroomResponse by lazy { MutableLiveData<Boolean>() }
    val leaveSecretChatroomResponse: LiveData<Boolean> = _leaveSecretChatroomResponse

    private val _deleteConversationsResponse by lazy { MutableLiveData<Int>() }
    val deleteConversationsResponse: LiveData<Int> = _deleteConversationsResponse

    sealed class ConversationEvent {
        data class NewConversation(val conversations: List<ConversationViewData>) :
            ConversationEvent()

        data class UpdatedConversation(val conversations: List<ConversationViewData>) :
            ConversationEvent()

        data class PostedConversation(val conversation: ConversationViewData) :
            ConversationEvent()
    }

    private val conversationEventChannel = Channel<ConversationEvent>(Channel.BUFFERED)
    val conversationEventFlow = conversationEventChannel.receiveAsFlow()

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageFlow = errorEventChannel.receiveAsFlow()

    //Job for preview link API's calls
    private var previewLinkJob: Job? = null

    //Variable to hold current preview link, helps to avoid duplicate API calls
    private var previewLink: String? = null

    sealed class ErrorMessageEvent {
        data class FollowChatroom(val errorMessage: String?) : ErrorMessageEvent()
        data class LeaveSecretChatroom(val errorMessage: String?) : ErrorMessageEvent()
        data class MuteChatroom(val errorMessage: String?) : ErrorMessageEvent()
        data class SetChatroomTopic(val errorMessage: String?) : ErrorMessageEvent()
        data class PostConversation(val errorMessage: String?) : ErrorMessageEvent()
        data class EditConversation(val errorMessage: String?) : ErrorMessageEvent()
        data class DeleteConversation(val errorMessage: String?) : ErrorMessageEvent()
    }

    private fun getChatroom() = chatroomDetail.chatroom

    fun getChatroomActions(): List<ChatroomActionViewData>? = chatroomDetail.actions

    private fun getConversationListShimmerView() = ConversationListShimmerViewData()

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
        return getChatroomViewData()?.memberViewData?.id == userPreferences.getMemberId()
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

    fun getOtherDmMember(): MemberViewData? {
        return null
        // todo:
//        return if (
//            userPreferences.getMemberId() == getChatroom()?.chatroomWithUser?.id
//        ) {
//            getChatroom()?.memberViewData
//        }
//        else {
//            getChatroom()?.chatroomWithUser
//        }
    }

    //get first normal or poll conversation for list
    fun getFirstNormalOrPollConversation(items: List<BaseViewType>): ConversationViewData? {
        return items.firstOrNull {
            it is ConversationViewData && ConversationState.isPollOrNormal(it.state)
        } as? ConversationViewData
    }

    fun fetchUriDetails(
        context: Context,
        uris: List<Uri>,
        callback: (media: List<MediaViewData>) -> Unit,
    ) {
        mediaRepository.getLocalUrisDetails(context, uris, callback)
    }

    fun syncChatroom(
        context: Context,
        chatroomId: String
    ): Pair<MediatorLiveData<WorkInfo.State>, Boolean> {
        val getChatroomRequest = GetChatroomRequest.Builder()
            .chatroomId(chatroomId)
            .build()
        val getChatroomResponse = lmChatClient.getChatroom(getChatroomRequest)
        val chatroom = getChatroomResponse.data?.chatroom

        //if conversation is stored for the first time
        //then start reopen sync
        //else first sync
        return if (chatroom?.isConversationStored == true) {
            //reopen worker
            setIsFirstTimeSync(false)
            val worker = lmChatClient.loadConversations(
                context,
                LoadConversationType.REOPEN,
                chatroomId
            )
            Pair(worker, false)
        } else {
            setIsFirstTimeSync(true)
            val worker = lmChatClient.loadConversations(
                context,
                LoadConversationType.FIRST_TIME,
                chatroomId
            )
            Pair(worker, true)
        }
    }

    fun setIsFirstTimeSync(value: Boolean) {
        isFirstSyncOngoing = value
    }

    fun startBackgroundFirstSync(
        context: Context,
        chatroomId: String
    ): MediatorLiveData<WorkInfo.State> {
        return lmChatClient.loadConversations(
            context,
            LoadConversationType.FIRST_TIME_BACKGROUND,
            chatroomId
        )
    }

    /**
     * Fetches the initial data for the current chatroom and pass it to fragment using live data
     * @param chatroomDetailExtras Chatroom Intent Extras
     *
     * 1st case ->
     * chatroom is not present
     *
     * 2nd case ->
     * chatroom is present but chatroom is deleted
     *
     * 3rd case ->
     * open a conversation directly through search/deep links
     *
     * 4th case ->
     * chatroom is present and conversation is not present
     *
     * 5th case ->
     * chatroom is opened through deeplink/explore feed, which is open for the first time
     *
     * 6th case ->
     * chatroom is present and conversation is present, chatroom opened for the first time from home feed
     *
     * 7th case ->
     * chatroom is present and conversation is present, chatroom has no unseen conversations
     *
     * 8th case ->
     * chatroom is present and conversation is present, chatroom has unseen conversations
     */
    fun getInitialData(chatroomDetailExtras: ChatroomDetailExtras) {
        viewModelScope.launchIO {
            val request =
                GetChatroomRequest.Builder().chatroomId(chatroomDetailExtras.chatroomId).build()
            val getChatroomResponse = lmChatClient.getChatroom(request)
            val chatroom = getChatroomResponse.data?.chatroom
            val dataList = mutableListOf<BaseViewType>()
            //1st case -> chatroom is not present, if yes return
            if (chatroom == null) {
                Log.d(TAG, "case 1")
                chatroomWasNotLoaded = true
                dataList.add(getConversationListShimmerView())
                sendInitialDataToUI(
                    InitialViewData.Builder()
                        .data(dataList)
                        .build()
                )
                return@launchIO
            }

            //2nd case -> chatroom is deleted, if yes return
            if (chatroom.deletedBy != null) {
                Log.d(TAG, "case 2")
                sendInitialDataToUI()
                return@launchIO
            }

            val getMemberRequest = GetMemberRequest.Builder()
                .memberId(userPreferences.getMemberId())
                .build()
            currentMemberFromDb =
                ViewDataConverter.convertMember(lmChatClient.getMember(getMemberRequest).data?.member)

            val chatroomViewData = ViewDataConverter.convertChatroom(
                chatroom,
                userPreferences.getMemberId(),
                ChatroomUtil.getChatroomViewType(chatroom)
            ) ?: return@launchIO
            chatroomDetail = ChatroomDetailViewData.Builder()
                .isMemberNotPartOfCommunity(currentMemberFromDb == null)
                .chatroom(chatroomViewData)
                .currentMember(currentMemberFromDb)
                .build()

            _canMemberRespond.postValue(true)

            val medianConversationId = if (!chatroomDetailExtras.conversationId.isNullOrEmpty()) {
                chatroomDetailExtras.conversationId
            } else if (!chatroomDetailExtras.reportedConversationId.isNullOrEmpty()) {
                chatroomDetailExtras.reportedConversationId
            } else {
                null
            }

            val initialData = when {
                //3rd case -> open a conversation directly through search/deep links
                medianConversationId != null -> {
                    Log.d(TAG, "case 3")
                    dataList.addAll(
                        fetchIntermediateConversations(
                            chatroomViewData,
                            medianConversationId = medianConversationId
                        )
                    )
                    dataList.addAll(getActionViewList())
                    val scrollIndex =
                        getFirstTimeScrollIndex(chatroom.lastSeenConversation, dataList)
                    InitialViewData.Builder()
                        .chatroom(chatroomViewData)
                        .data(dataList)
                        .scrollPosition(scrollIndex)
                        .build()
                }
                //4th case -> chatroom is present and conversation is not present
                chatroomViewData.totalAllResponseCount == 0 -> {
                    Log.d(TAG, "case 4")
                    dataList.add(getDateView(chatroomViewData.date))
                    dataList.add(chatroomViewData)
                    dataList.addAll(getActionViewList())
                    InitialViewData.Builder()
                        .chatroom(chatroomViewData)
                        .data(dataList)
                        .scrollPosition(SCROLL_UP)
                        .build()
                }
                //5th case -> chatroom is opened through deeplink/explore feed, which is open for the first time
                chatroomWasNotLoaded -> {
                    Log.d(TAG, "case 5")
                    dataList.addAll(fetchBottomConversations(chatroomViewData))
                    dataList.addAll(getActionViewList())
                    chatroomWasNotLoaded = false
                    InitialViewData.Builder()
                        .chatroom(chatroomViewData)
                        .data(dataList)
                        .scrollPosition(SCROLL_DOWN)
                        .build()
                }
                //6th case -> chatroom is present and conversation is present, chatroom opened for the first time from home feed
                chatroom.lastSeenConversation == null || chatroomDetailExtras.loadFromTop == true -> {
                    Log.d(TAG, "case 6")
                    dataList.add(getConversationListShimmerView())
                    InitialViewData.Builder()
                        .chatroom(chatroomViewData)
                        .data(dataList)
                        .build()
                }
                //7th case -> chatroom is present but conversations are not stored in chatroom
                !chatroom.isConversationStored -> {
                    Log.d(TAG, "case 7")
                    dataList.add(getConversationListShimmerView())
                    InitialViewData.Builder()
                        .chatroom(chatroomViewData)
                        .data(dataList)
                        .build()
                }
                //8th case -> chatroom is present and conversation is present, chatroom has no unseen conversations
                chatroom.unseenCount == 0 -> {
                    Log.d(TAG, "case 8")
                    dataList.addAll(fetchBottomConversations(chatroomViewData))
                    dataList.addAll(getActionViewList())
                    InitialViewData.Builder()
                        .chatroom(chatroomViewData)
                        .data(dataList)
                        .scrollPosition(SCROLL_DOWN)
                        .build()
                }
                //9th case -> chatroom is present and conversation is present, chatroom has unseen conversations
                else -> {
                    Log.d(TAG, "case 9")
                    dataList.addAll(
                        fetchIntermediateConversations(
                            chatroomViewData,
                            medianConversation = chatroom.lastSeenConversation
                        )
                    )
                    dataList.addAll(getActionViewList())
                    val scrollIndex =
                        getFirstTimeScrollIndex(chatroom.lastSeenConversation, dataList)
                    InitialViewData.Builder()
                        .chatroom(chatroomViewData)
                        .data(dataList)
                        .scrollPosition(scrollIndex)
                        .build()
                }
            }
            sendInitialDataToUI(initialData)
            fetchChatroomFromNetwork()
            markChatroomAsRead(chatroomDetailExtras.chatroomId)
            fetchMemberState()
            observeConversations(chatroomDetailExtras.chatroomId)
            // todo: write in db
//            observeCommunity(chatroomViewData.communityId)
        }
    }

    private fun sendInitialDataToUI(data: InitialViewData? = null) {
        viewModelScope.launchMain {
            _initialData.value = data
        }
    }

    /**
     * Returns the data list to show on the chatroom screen recyclerview
     * @return Pair(Data List, Scroll Position)
     */
    private fun getActionViewList(): List<BaseViewType> {
        val dataList = mutableListOf<BaseViewType>()
        val chatroom = getChatroom()
        when {
            chatroom?.showFollowAutoTag == true -> {
                dataList.add(AutoFollowedTaggedActionViewData())
            }

            chatroom?.showFollowTelescope == true -> {
                dataList.add(FollowItemViewData())
            }
        }
        return dataList
    }

    /**
     * Returns the recyclerview position to show when the user opens the chatroom
     * We always show the last seen conversation at the bottom of the screen in visible portion
     * @param lastSeenConversation The last seen conversation
     * @param items The final recyclerview items
     */
    private fun getFirstTimeScrollIndex(
        lastSeenConversation: Conversation?,
        items: List<BaseViewType>,
    ): Int {
        return items.indexOfFirst {
            it is ConversationViewData && it.id == lastSeenConversation?.id
        }
    }

    /**
     * chatroom is present and conversation is present, chatroom has unseen conversations
     * 1. Fetch the chatroom
     * 2. Find the median (unseen conversation timestamp) from chatroom property
     * 3. Fetch 50 conversations >= than the median timestamp, sorted by ascending on TIMESTAMP | ID and append it at bottom
     * 4. Fetch 50 conversations <= than the median timestamp, sorted by descending on TIMESTAMP | ID , reverse the list and append it at top
     * 5. If the chatroom contains conversations <= 101, then append the chatroom at the top, else don't append
     */
    private fun fetchIntermediateConversations(
        chatroomViewData: ChatroomViewData,
        medianConversation: Conversation? = null,
        medianConversationId: String? = null,
    ): List<BaseViewType> {
        val getConversationRequest = GetConversationRequest.Builder()
            .conversationId(medianConversationId ?: "")
            .build()
        val getConversationResponse = lmChatClient.getConversation(getConversationRequest)
        val median = medianConversation
            ?: getConversationResponse.data?.conversation
            ?: return emptyList()
        val medianViewData = ViewDataConverter.convertConversation(median) ?: return emptyList()

        val dataList = mutableListOf<BaseViewType>()

        val getAboveConversationsRequest = GetConversationsRequest.Builder()
            .conversation(median)
            .chatroomId(chatroomViewData.id)
            .type(GetConversationType.ABOVE)
            .limit(CONVERSATIONS_LIMIT)
            .build()
        val getAboveConversationsResponse =
            lmChatClient.getConversations(getAboveConversationsRequest)
        val aboveConversations = getAboveConversationsResponse.data?.conversations ?: emptyList()
        val aboveConversationsViewData = ViewDataConverter.convertConversations(aboveConversations)

        val getBelowConversationsRequest = GetConversationsRequest.Builder()
            .conversation(median)
            .chatroomId(chatroomViewData.id)
            .type(GetConversationType.BELOW)
            .limit(CONVERSATIONS_LIMIT)
            .build()
        val getBelowConversationsResponse =
            lmChatClient.getConversations(getBelowConversationsRequest)
        val belowConversations = getBelowConversationsResponse.data?.conversations ?: emptyList()
        val belowConversationsViewData = ViewDataConverter.convertConversations(belowConversations)

        var conversations =
            (aboveConversationsViewData + medianViewData + belowConversationsViewData)

        if (aboveConversationsViewData.size < CONVERSATIONS_LIMIT
            || aboveConversationsViewData.firstOrNull()?.state == STATE_HEADER
        ) {
            dataList.add(chatroomViewData)
            val headerConversation = getHeaderConversation(conversations)
            if (headerConversation != null) {
                dataList.add(0, headerConversation)
                conversations = conversations.drop(1)
            }
        } else {
            val aboveTopConversationsCount =
                getConversationsAboveCount(aboveConversationsViewData.firstOrNull())
            if (aboveTopConversationsCount == 0) {
                dataList.add(chatroomViewData)
                val headerConversation = getHeaderConversation(conversations)
                if (headerConversation != null) {
                    dataList.add(0, headerConversation)
                    conversations = conversations.drop(1)
                }
            }
        }
        dataList.addAll(
            addDateViewToList(
                conversations,
                chatroomViewData,
                null
            )
        )
        return dataList
    }

    /**
     * chatroom is present and conversation is present, chatroom has no unseen conversations
     * ---chatroom will load from bottom, if conversations are limited, also add chatroom object---
     * 1. Fetch the chatroom and only append it if conversations count <= 50
     * 2. Fetch 50 conversations sorted by descending on TIMESTAMP | ID, reverse the list and append it at bottom
     */
    private fun fetchBottomConversations(
        chatroomViewData: ChatroomViewData,
    ): List<BaseViewType> {
        val dataList = mutableListOf<BaseViewType>()
        val getBottomConversationsRequest = GetConversationsRequest.Builder()
            .chatroomId(chatroomViewData.id)
            .type(GetConversationType.BOTTOM)
            .limit(CONVERSATIONS_LIMIT)
            .build()
        val getBottomConversationsResponse =
            lmChatClient.getConversations(getBottomConversationsRequest)
        val bottomConversations = getBottomConversationsResponse.data?.conversations ?: emptyList()

        var bottomConversationsViewData =
            ViewDataConverter.convertConversations(bottomConversations)
        if (chatroomViewData.totalAllResponseCount <= CONVERSATIONS_LIMIT) {
            //All conversations are fetched
            dataList.add(getDateView(chatroomViewData.date))
            dataList.add(chatroomViewData)
            val headerConversation = getHeaderConversation(bottomConversationsViewData)
            if (headerConversation != null) {
                dataList.add(0, headerConversation)
                bottomConversationsViewData = bottomConversationsViewData.drop(1)
            }
        }
        dataList.addAll(
            addDateViewToList(
                bottomConversationsViewData,
                chatroomViewData,
                null
            )
        )
        return dataList
    }

    /**
     * Observe current chatroom conversations
     * @param chatroomId
     */
    private fun observeConversations(chatroomId: String) {
        viewModelScope.launchMain {
            val conversationChangeListener = object : ConversationChangeListener {
                override fun getChangedConversations(conversations: List<Conversation>?) {
                    if (!conversations.isNullOrEmpty()) {
                        val conversationsViewData =
                            ViewDataConverter.convertConversations(conversations)
                        sendConversationUpdatesToUI(conversationsViewData)
                    }
                }

                override fun getNewConversations(conversations: List<Conversation>?) {
                    if (!conversations.isNullOrEmpty()) {
                        val conversationsViewData =
                            ViewDataConverter.convertConversations(conversations)
                        sendNewConversationsToUI(conversationsViewData)
                    }
                }

                override fun getPostedConversations(conversations: List<Conversation>?) {
                    if (!conversations.isNullOrEmpty()) {
                        val conversationsViewData =
                            ViewDataConverter.convertConversations(conversations)
                        sendConversationUpdatesToUI(conversationsViewData)
                    }
                }
            }

            val observeConversationsRequest = ObserveConversationsRequest.Builder()
                .chatroomId(chatroomId)
                .listener(conversationChangeListener)
                .build()
            lmChatClient.observeConversations(observeConversationsRequest)
        }
    }

    /**
     * Send updates to UI using live data
     * @param conversations List of conversations
     */
    private fun sendConversationUpdatesToUI(conversations: List<ConversationViewData>) {
        val value = conversations.sortedBy {
            it.createdEpoch
        }
        viewModelScope.launchDefault {
            conversationEventChannel.send(ConversationEvent.UpdatedConversation(value))
        }
    }

    /**
     * Send updates to UI using live data
     * @param conversations List of conversations
     */
    private fun sendNewConversationsToUI(conversations: List<ConversationViewData>) {
        if (isFirstSyncOngoing) return
        val value = conversations.sortedBy {
            it.createdEpoch
        }
        viewModelScope.launchDefault {
            conversationEventChannel.send(ConversationEvent.NewConversation(value))
        }
    }

    /**
     * Fetch the top conversations on header click
     */
    fun fetchTopConversationsOnClick(
        topConversation: ConversationViewData,
        oldConversations: List<BaseViewType>,
        repliedChatRoomId: String? = null,
    ) {
        viewModelScope.launchIO {
            val topConversationsCount = getConversationsAboveCount(topConversation)
            if (topConversationsCount <= CONVERSATIONS_LIMIT) {
                val data = fetchPaginatedData(
                    SCROLL_UP,
                    topConversation,
                    oldConversations,
                    SCROLL_UP,
                    repliedChatRoomId = repliedChatRoomId
                )
                if (data != null) {
                    viewModelScope.launchMain {
                        _paginatedData.value = data
                    }
                }
            } else {
                val conversations = fetchTopConversations(getChatroomViewData())
                viewModelScope.launchMain {
                    _scrolledData.value = PaginatedViewData.Builder()
                        .scrollState(SCROLL_UP)
                        .data(conversations)
                        .repliedChatRoomId(repliedChatRoomId)
                        .build()
                }
            }
        }
    }

    /**
     * Fetch the top conversations on header click
     */
    fun fetchBottomConversationsOnClick(
        bottomConversation: ConversationViewData,
        oldConversations: List<BaseViewType>,
    ) {
        viewModelScope.launchIO {
            val bottomConversationsCount = getConversationsBelowCount(bottomConversation)
            if (bottomConversationsCount <= CONVERSATIONS_LIMIT) {
                val data = fetchPaginatedData(
                    SCROLL_DOWN,
                    bottomConversation,
                    oldConversations,
                    SCROLL_DOWN
                )
                if (data != null) {
                    viewModelScope.launchMain {
                        _paginatedData.value = data
                    }
                }
            } else {
                val conversations = fetchBottomConversations(getChatroomViewData()!!)
                viewModelScope.launchMain {
                    _scrolledData.value = PaginatedViewData.Builder()
                        .scrollState(SCROLL_DOWN)
                        .data(conversations)
                        .build()
                }
            }
        }
    }

    fun fetchPaginatedDataOnScroll(
        @ScrollState scrollState: Int,
        conversationKey: ConversationViewData,
        oldConversations: List<BaseViewType>,
    ) {
        viewModelScope.launchIO {
            val data = fetchPaginatedData(
                scrollState,
                conversationKey,
                oldConversations
            )
            if (data != null) {
                viewModelScope.launchMain {
                    _paginatedData.value = data
                }
            }
        }
    }

    private fun fetchPaginatedData(
        @ScrollState scrollState: Int,
        conversationKey: ConversationViewData,
        oldConversations: List<BaseViewType>,
        @ScrollState extremeScrollTo: Int? = null,
        repliedConversationId: String? = null,
        repliedChatRoomId: String? = null,
    ): PaginatedViewData? {
        val chatroom = getChatroom() ?: return null
        val chatroomId = chatroom.id
        val conversationForRequest = ViewDataConverter.convertConversation(conversationKey)
        val getConversationsResponse = if (scrollState == SCROLL_UP) {
            lmChatClient.getConversations(
                GetConversationsRequest.Builder()
                    .chatroomId(chatroomId)
                    .conversation(conversationForRequest)
                    .type(GetConversationType.ABOVE)
                    .limit(CONVERSATIONS_LIMIT)
                    .build()
            )
        } else {
            lmChatClient.getConversations(
                GetConversationsRequest.Builder()
                    .chatroomId(chatroomId)
                    .conversation(conversationForRequest)
                    .type(GetConversationType.BELOW)
                    .limit(CONVERSATIONS_LIMIT)
                    .build()
            )
        }
        val conversations = getConversationsResponse.data?.conversations ?: emptyList()
        var conversationsViewData = ViewDataConverter.convertConversations(conversations)
        if (conversationsViewData.isNotEmpty()) {
            /**
             * If there are no more conversations, and is scrolling up, add the chatroom to the list
             * Also add the header conversation at the top of the chatroom item
             */
            val oldConversationsCount = getConversationsCount(oldConversations)
            val totalConversationsCount = oldConversationsCount + conversationsViewData.size
            val totalAllResponseCount = chatroom.totalAllResponseCount
            return if (
                scrollState == SCROLL_UP &&
                totalConversationsCount == totalAllResponseCount
            ) {
                val dataList = mutableListOf<BaseViewType>()
                dataList.add(getDateView(chatroom.date))
                dataList.add(chatroom)
                val header = getHeaderConversation(conversationsViewData)
                if (header != null) {
                    dataList.add(0, header)
                    conversationsViewData = conversationsViewData.drop(1)
                }
                dataList.addAll(
                    addDateViewToList(
                        conversationsViewData,
                        chatroomDetail.chatroom,
                        null
                    )
                )
                PaginatedViewData.Builder()
                    .scrollState(scrollState)
                    .data(dataList)
                    .extremeScrollTo(extremeScrollTo)
                    .repliedChatRoomId(repliedChatRoomId)
                    .repliedConversationId(repliedConversationId)
                    .build()
            } else {
                PaginatedViewData.Builder()
                    .scrollState(scrollState)
                    .data(
                        addDateViewToList(
                            conversationsViewData,
                            chatroomDetail.chatroom,
                            oldConversations.lastOrNull()
                        )
                    )
                    .extremeScrollTo(extremeScrollTo)
                    .repliedChatRoomId(repliedChatRoomId)
                    .repliedConversationId(repliedConversationId)
                    .build()
            }
        }
        return null
    }

    private fun getConversationsCount(items: List<BaseViewType>) = items.count {
        it is ConversationViewData
    }

    /**
     * chatroom is present and conversation is present, chatroom opened for the first time
     * ---chatroom will load from top with chatroom object and limited conversations---
     * 1. Fetch the chatroom and append it at top
     * 2. Fetch limited conversations sorted by ascending on TIMESTAMP & ID and append it at bottom
     */
    private fun fetchTopConversations(
        chatroom: ChatroomViewData?,
    ): List<BaseViewType> {
        if (chatroom == null) {
            return emptyList()
        }
        val dataList = mutableListOf<BaseViewType>()
        dataList.add(getDateView(chatroom.date))
        dataList.add(chatroom)
        val getConversationsResponse = lmChatClient.getConversations(
            GetConversationsRequest.Builder()
                .chatroomId(chatroom.id)
                .type(GetConversationType.TOP)
                .limit(CONVERSATIONS_LIMIT)
                .build()
        )
        val topConversations = getConversationsResponse.data?.conversations ?: emptyList()
        var topConversationsViewData = ViewDataConverter.convertConversations(topConversations)
        val headerConversation = getHeaderConversation(topConversationsViewData)
        if (headerConversation != null) {
            dataList.add(0, headerConversation)
            topConversationsViewData = topConversationsViewData.drop(1)
        }
        dataList.addAll(
            addDateViewToList(topConversationsViewData, chatroom, null)
        )
        return dataList
    }

    private fun getHeaderConversation(
        conversations: List<BaseViewType>,
    ): ConversationViewData? {
        return conversations.firstOrNull { item ->
            item is ConversationViewData && item.state == STATE_HEADER
        } as? ConversationViewData
    }

    /**
     * Function to insert date view inside a conversations list
     * @param conversations list of conversation
     * @param chatroomViewData chatroom object
     * @param lastItem The last item of the list after which the [conversations] will be appended
     */
    private fun addDateViewToList(
        conversations: List<ConversationViewData>,
        chatroomViewData: ChatroomViewData? = null,
        lastItem: BaseViewType? = null,
    ): List<BaseViewType> {
        val dataList = mutableListOf<BaseViewType>()
        conversations.withIndex().forEach { item ->
            val previousIndex = item.index - 1
            if (previousIndex > -1) {
                val previousConversation = conversations[previousIndex]
                val currentConversation = conversations[item.index]
                if (previousConversation.date != currentConversation.date) {
                    //add date if 2 consecutive conversations have different date value
                    dataList.add(getDateView(currentConversation.date))
                }
                //add the conversation
                dataList.add(item.value)
            } else {
                when {
                    lastItem is ConversationViewData -> {
                        if (lastItem.date != item.value.date) {
                            dataList.add(getDateView(item.value.date))
                        }
                    }

                    lastItem is ChatroomDateViewData -> {
                        if (lastItem.date != item.value.date) {
                            dataList.add(getDateView(item.value.date))
                        }
                    }

                    chatroomViewData?.date != item.value.date -> {
                        dataList.add(getDateView(item.value.date))
                    }
                }
                //add the first conversation
                dataList.add(item.value)
            }
        }
        return dataList
    }

    fun isShimmerPresent(items: List<BaseViewType>): Boolean {
        return items.firstOrNull {
            it is ConversationListShimmerViewData
        } != null
    }

    private fun getDateView(date: String?): ChatroomDateViewData {
        return ChatroomDateViewData.Builder()
            .date(date)
            .build()
    }

    fun createTemporaryAutoFollowAndTopicConversation(
        state: Int,
        message: String,
    ): ConversationViewData {
        val memberViewData = currentMemberFromDb ?: MemberViewData.Builder().build()
        return ConversationViewData.Builder()
            .id("-")
            .state(state)
            .memberViewData(memberViewData)
            .createdEpoch(System.currentTimeMillis())
            .answer(
                "${
                    Route.createRouteForMemberProfile(
                        currentMemberFromDb,
                        getChatroom()?.communityId
                    )
                } $message"
            )
            .build()
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
        aboveConversation: ConversationViewData?,
    ): Int {
        if (aboveConversation == null) {
            return 0
        }
        val conversation = ViewDataConverter.convertConversation(aboveConversation)
        val getConversationsCountRequest = GetConversationsCountRequest.Builder()
            .chatroomId(getChatroom()?.id ?: "")
            .conversation(conversation)
            .type(GetConversationCountType.ABOVE)
            .build()
        return lmChatClient.getConversationsCount(getConversationsCountRequest).data?.count ?: 0
    }


    private fun getConversationsBelowCount(
        bottomConversation: ConversationViewData?,
    ): Int {
        if (bottomConversation == null) {
            return 0
        }
        val conversation = ViewDataConverter.convertConversation(bottomConversation)
        val getConversationsCountRequest = GetConversationsCountRequest.Builder()
            .chatroomId(getChatroom()?.id ?: "")
            .conversation(conversation)
            .type(GetConversationCountType.BELOW)
            .build()
        return lmChatClient.getConversationsCount(getConversationsCountRequest).data?.count ?: 0
    }

    fun getContentDownloadSettings(communityId: String) {
        // todo:
    }

    fun setLastSeenTrueAndSaveDraftResponse(
        chatroomId: String,
        draftText: String?
    ) {
        val updateLastSeenAndDraftRequest = UpdateLastSeenAndDraftRequest.Builder()
            .chatroomId(chatroomId)
            .draft(draftText)
            .build()
        lmChatClient.updateLastSeenAndDraft(updateLastSeenAndDraftRequest)
        markChatroomAsRead(chatroomId)
    }

    fun observeLiveConversations(context: Context, chatroomId: String) {
        viewModelScope.launchIO {
            lmChatClient.observeLiveConversations(context, chatroomId)
        }
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
                .memberId(userPreferences.getMemberId())
                .value(value)
                .build()

            // call api
            val response = lmChatClient.followChatroom(request)
            if (response.success) {
                // Update the ChatRoom actions once CM joins the chatroom
                if (value && getChatroom()?.isSecret == true) {
                    fetchChatroomFromNetwork()
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
                .chatroomId(chatroomId)
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

    private fun fetchChatroomFromNetwork() {
        viewModelScope.launchIO {
            val request = GetChatroomActionsRequest.Builder()
                .chatroomId(chatroomDetail.chatroom?.id ?: "")
                .build()

            val getChatroomActionsResponse = lmChatClient.getChatroomActions(request)
            val data = getChatroomActionsResponse.data ?: return@launchIO
            chatroomDetail = chatroomDetail.toBuilder()
                .actions(ViewDataConverter.convertChatroomActions(data.chatroomActions))
                .placeholderText(data.placeHolder)
                .participantCount(data.participantCount)
                .canAccessSecretChatRoom(data.canAccessSecretChatroom)
                .build()
            _chatroomDetailLiveData.postValue(chatroomDetail)
        }
    }

    private fun markChatroomAsRead(chatroomId: String) {
        viewModelScope.launchIO {
            val request = MarkReadChatroomRequest.Builder()
                .chatroomId(chatroomId)
                .build()

            val response = lmChatClient.markReadChatroom(request)
            if (response.success) {
                Log.d(SDKApplication.LOG_TAG, "mark read chatroom success.")
            } else {
                Log.e(
                    SDKApplication.LOG_TAG,
                    "mark read chatroom failed: ${response.errorMessage}"
                )
            }
        }
    }

    private fun fetchMemberState() {
        viewModelScope.launch {
            // todo:
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
                .chatroomId(chatroomId)
                .conversationId(conversation.id)
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
                .chatroomId(chatroomId)
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
            viewModelScope.launchIO {
                val conversationIds = conversations.map {
                    it.id
                }
                val request = DeleteConversationsRequest.Builder()
                    .conversationIds(conversationIds)
                    .build()

                val response = lmChatClient.deleteConversations(request)
                if (response.success) {
                    postConversationsDeleted(conversations)
                    _deleteConversationsResponse.postValue(response.data?.conversations?.size)
                    sendMessageDeletedEvent(conversations)
                } else {
                    errorEventChannel.send(ErrorMessageEvent.DeleteConversation(response.errorMessage))
                }
            }
        }
    }

    private fun postConversationsDeleted(conversations: List<ConversationViewData>) {
        sendConversationUpdatesToUI(conversations.map {
            it.toBuilder().deletedBy(userPreferences.getMemberId()).build()
        })
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

    @SuppressLint("CheckResult")
    fun postConversation(
        context: Context,
        text: String,
        fileUris: List<SingleUriData>?,
        shareLink: String?,
        replyConversationId: String?,
        replyChatRoomId: String?,
        taggedUsers: List<TagViewData>,
        replyChatData: ChatReplyViewData?
    ) {
        viewModelScope.launchIO {
            val chatroomId = chatroomDetail.chatroom?.id ?: return@launchIO
            val communityId = chatroomDetail.chatroom?.communityId
            val temporaryId = ValueUtils.getTemporaryId()
            val updatedFileUris = includeAttachmentMetaData(context, fileUris)
            var postConversationRequestBuilder = PostConversationRequest.Builder()
                .chatroomId(chatroomId)
                .text(text)
                .repliedConversationId(replyConversationId)
                .attachmentCount(updatedFileUris?.size)
                .temporaryId(temporaryId)
                .repliedChatroomId(replyChatRoomId)

            if (!shareLink.isNullOrEmpty()) {
                when {
                    Patterns.EMAIL_ADDRESS.matcher(shareLink).matches() -> {
                        postConversationRequestBuilder =
                            postConversationRequestBuilder.ogTags(null).shareLink(null)
                    }

                    linkOgTags.value?.url == shareLink &&
                            isValidLinkViewData(linkOgTags.value) -> {
                        postConversationRequestBuilder = postConversationRequestBuilder
                            .ogTags(
                                ViewDataConverter.convertLinkOGTags(linkOgTags.value)
                            ).shareLink(shareLink)
                    }

                    isURLReachable(shareLink) -> {
                        postConversationRequestBuilder =
                            postConversationRequestBuilder.shareLink(shareLink)
                    }
                }
            }
            val postConversationRequest = postConversationRequestBuilder.build()

            val temporaryConversation = saveTemporaryConversation(
                userPreferences.getMemberId(),
                communityId,
                postConversationRequest,
                updatedFileUris
            )
            sendPostedConversationsToUI(temporaryConversation)

            val response = lmChatClient.postConversation(postConversationRequest)
            if (response.success) {
                val data = response.data
                onConversationPosted(
                    context,
                    data,
                    updatedFileUris,
                    temporaryConversation,
                    taggedUsers,
                    replyChatData,
                    replyConversationId,
                    replyChatRoomId
                )
            } else {
                errorEventChannel.send(ErrorMessageEvent.PostConversation(response.errorMessage))
            }
        }
    }

    /**
     * Includes attachment's meta data such as dimensions, thumbnails, etc
     * @param context
     * @param files List<SingleUriData>?
     */
    private fun includeAttachmentMetaData(
        context: Context,
        files: List<SingleUriData>?,
    ): List<SingleUriData>? {
        return files?.map {
            when (it.fileType) {
                IMAGE, GIF -> {
                    val dimensions = FileUtil.getImageDimensions(context, it.uri)
                    it.toBuilder().width(dimensions.first).height(dimensions.second).build()
                }

                VIDEO -> {
                    val thumbnailUri = FileUtil.getVideoThumbnailUri(context, it.uri)
                    if (thumbnailUri != null) {
                        it.toBuilder().thumbnailUri(thumbnailUri).build()
                    } else {
                        it
                    }
                }

                else -> it
            }
        }
    }

    private fun isValidLinkViewData(linkViewData: LinkOGTagsViewData?): Boolean {
        if (linkViewData == null)
            return false
        return linkViewData.description != null && linkViewData.image != null && linkViewData.title != null
    }

    private suspend fun isURLReachable(link: String?): Boolean {
        return viewModelScope.async(Dispatchers.IO) {
            try {
                val url = URL(link)
                val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.connectTimeout = 5 * 1000
                httpURLConnection.connect()
                httpURLConnection.responseCode == 200
            } catch (exception: Exception) {
                Log.d(TAG, exception.message.toString())
                false
            }
        }.await()
    }

    private fun saveTemporaryConversation(
        memberId: String,
        communityId: String?,
        request: PostConversationRequest,
        fileUris: List<SingleUriData>?
    ): ConversationViewData? {
        val conversation = ViewDataConverter.convertConversation(
            memberId,
            communityId,
            request,
            fileUris
        )
        val saveConversationRequest = SaveConversationRequest.Builder()
            .conversation(conversation)
            .build()
        lmChatClient.saveTemporaryConversation(saveConversationRequest)
        val replyConversation = if (conversation.replyConversationId != null) {
            val getConversationRequest = GetConversationRequest.Builder()
                .conversationId(conversation.replyConversationId ?: "")
                .build()
            lmChatClient.getConversation(getConversationRequest).data?.conversation
        } else {
            null
        }

        val getMemberRequest = GetMemberRequest.Builder()
            .memberId(conversation.memberId ?: "")
            .build()
        val member = lmChatClient.getMember(getMemberRequest).data?.member
        val memberViewData = ViewDataConverter.convertMember(member)
        return ViewDataConverter.convertConversation(conversation, memberViewData)
            ?.toBuilder()
            ?.replyConversation(ViewDataConverter.convertConversation(replyConversation))
            ?.build()
    }

    /**
     * Send updates to UI using live data
     * @param conversation conversation object
     */
    private fun sendPostedConversationsToUI(conversation: ConversationViewData?) {
        if (conversation == null) {
            return
        }
        viewModelScope.launchDefault {
            conversationEventChannel.send(ConversationEvent.PostedConversation(conversation))
        }
    }

    private fun onConversationPosted(
        context: Context,
        response: PostConversationResponse?,
        updatedFileUris: List<SingleUriData>?,
        tempConversation: ConversationViewData?,
        taggedUsers: List<TagViewData>,
        replyChatData: ChatReplyViewData?,
        replyConversationId: String?,
        replyChatRoomId: String?
    ) {
        if (response?.conversation != null) {
            var uploadData: Pair<WorkContinuation?, String>? = null
            val requestList = ArrayList<GenericFileRequest>()
            if (!updatedFileUris.isNullOrEmpty()) {
                uploadData = uploadFilesViaWorker(context, response.id!!, updatedFileUris.size)
                requestList.addAll(
                    getUploadFileRequestList(
                        context,
                        updatedFileUris,
                        chatroomDetail.chatroom?.id,
                        response.id
                    )
                )
            }
            postedConversation(
                taggedUsers,
                tempConversation,
                replyChatData,
                replyConversationId,
                replyChatRoomId
            )
            // Start Uploading Files
            uploadData?.first?.enqueue()
        }
    }

    private fun getUploadFileRequestList(
        context: Context,
        fileUris: List<SingleUriData>,
        chatroomId: String?,
        conversationId: String?,
    ): List<GenericFileRequest> {
        return fileUris.mapIndexed { index, attachment ->
            val attachmentMeta = AttachmentMetaViewData.Builder()
                .duration(attachment.duration)
                .numberOfPage(attachment.pdfPageCount)
                .size(attachment.size)
                .build()
            var builder = GenericFileRequest.Builder()
                .name(attachment.mediaName)
                .fileUri(attachment.uri)
                .fileType(attachment.fileType)
                .width(attachment.width)
                .height(attachment.height)
                .meta(attachmentMeta)
                .index(index)

            val localFilePath = FileUtil.getRealPath(context, attachment.uri).path
            val file = File(localFilePath)
            val serverPath = UploadHelper.getConversationAttachmentFilePath(
                chatroomId,
                conversationId,
                attachment.fileType,
                file
            )

            //Add thumbnail is present
            if (attachment.thumbnailUri != null) {
                val thumbnailUri = attachment.thumbnailUri
                val thumbnailLocalFilePath = FileUtil.getRealPath(context, thumbnailUri).path
                val thumbnailFile = File(thumbnailLocalFilePath)
                val thumbnailAWSFolderPath = UploadHelper.getConversationAttachmentFilePath(
                    chatroomId,
                    conversationId,
                    attachment.fileType,
                    thumbnailFile,
                    isThumbnail = true
                )
                builder = builder.thumbnailUri(thumbnailUri)
                    .thumbnailLocalFilePath(thumbnailLocalFilePath)
                    .thumbnailAWSFolderPath(thumbnailAWSFolderPath)
            }

            builder.localFilePath(localFilePath).awsFolderPath(serverPath).build()
        }
    }

    private fun postedConversation(
        taggedUsers: List<TagViewData>,
        conversationViewData: ConversationViewData?,
        replyChatData: ChatReplyViewData? = null,
        replyConversationId: String? = null,
        replyChatRoomId: String? = null
    ) {
        if (replyChatData != null) {
            sendMessageReplyEvent(
                conversationViewData,
                replyChatData.repliedMemberId,
                replyChatData.repliedMemberState,
                replyConversationId ?: replyChatRoomId,
                replyChatData.type
            )
        }
        sendChatroomResponded(taggedUsers, conversationViewData)
        if (ChatroomUtil.getConversationType(conversationViewData) == VOICE_NOTE) {
            sendVoiceNoteSent(conversationViewData?.id)
        }
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
        val updateConversationUploadWorkerUUIDRequest =
            UpdateConversationUploadWorkerUUIDRequest.Builder()
                .conversationId(conversationId)
                .uuid(uploadData.second)
                .build()
        lmChatClient.updateConversationUploadWorkerUUID(updateConversationUploadWorkerUUIDRequest)
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

    fun postEditedChatRoom(text: String, chatroom: ChatroomViewData) {
        viewModelScope.launchIO {
            // todo:
//            val request = EditChatroomRequest.Builder()
//                .text(text)
//                .chatroomId(chatroom.id())
//                .build()
        }
    }

    fun postEditedConversation(
        text: String,
        shareLink: String?,
        conversation: ConversationViewData,
    ) {
        viewModelScope.launchIO {
            val request = EditConversationRequest.Builder()
                .conversationId(conversation.id)
                .text(text)
                .shareLink(shareLink)
                .build()

            val response = lmChatClient.editConversation(request)
            if (response.success) {
                postedEditedConversation(text, conversation)
                sendMessageEditedEvent(conversation)
            } else {
                errorEventChannel.send(ErrorMessageEvent.EditConversation(response.errorMessage))
            }
        }
    }

    private fun postedEditedConversation(
        text: String,
        conversation: ConversationViewData,
    ) {
        val updatedConversation = conversation.toBuilder()
            .answer(text)
            .isEdited(true)
            .build()
        sendConversationUpdatesToUI(listOf(updatedConversation))
    }

    fun resendFailedConversation(context: Context, conversation: ConversationViewData) {
        postFailedConversation(context, conversation)
    }

    private fun postFailedConversation(context: Context, conversation: ConversationViewData) {
        viewModelScope.launchIO {
            val chatroomId = chatroomDetail.chatroom?.id ?: return@launchIO
            val postConversationRequest = PostConversationRequest.Builder()
                .chatroomId(chatroomId)
                .text(conversation.answer)
                .shareLink(conversation.ogTags?.url)
                .ogTags(ViewDataConverter.convertLinkOGTags(conversation.ogTags))
                .repliedConversationId(conversation.replyConversation?.id)
                .repliedChatroomId(conversation.replyChatroomId)
                .attachmentCount(conversation.attachments?.size)
                .temporaryId(conversation.temporaryId)
                .build()

            val updateTemporaryConversationRequest = UpdateTemporaryConversationRequest.Builder()
                .conversationId(conversation.id)
                .localSavedEpoch(conversation.localCreatedEpoch ?: System.currentTimeMillis())
                .build()
            lmChatClient.updateTemporaryConversation(updateTemporaryConversationRequest)

            val response = lmChatClient.postConversation(postConversationRequest)
            if (response.success) {
                onFailedConversationPosted(
                    context,
                    conversation,
                    response.data
                )
            }
        }
    }

    private fun onFailedConversationPosted(
        context: Context,
        conversation: ConversationViewData,
        response: PostConversationResponse?,
    ) {
        if (response?.conversation != null) {
            var uploadData: Pair<WorkContinuation?, String>? = null
            val requestList = ArrayList<GenericFileRequest>()
            if (!conversation.attachments.isNullOrEmpty()) {
                uploadData = uploadFilesViaWorker(
                    context,
                    response.id!!,
                    conversation.attachmentCount ?: 0
                )
                requestList.addAll(getUploadFileRequestList(context, conversation))
            }

            postedConversation(
                emptyList(),
                conversation
            )

            // Start Uploading Files
            uploadData?.first?.enqueue()
        }
    }

    private fun getUploadFileRequestList(
        context: Context,
        conversation: ConversationViewData,
    ): List<GenericFileRequest> {
        return conversation.attachments.orEmpty().mapIndexed { index, attachment ->
            var builder = GenericFileRequest.Builder()
                .name(attachment.name ?: attachment.uri.lastPathSegment)
                .fileUri(attachment.uri)
                .fileType(attachment.type)
                .width(attachment.width)
                .height(attachment.height)
                .meta(attachment.meta)
                .index(index)

            val localFilePath = FileUtil.getRealPath(context, attachment.uri).path
            val file = File(localFilePath)
            val serverPath = UploadHelper.getConversationAttachmentFilePath(
                conversation.chatroomId,
                conversation.id,
                attachment.type,
                file
            )

            //Add thumbnail is present
            if (!attachment.thumbnail.isNullOrEmpty()) {
                val thumbnailUri = Uri.parse(attachment.thumbnail)
                val thumbnailLocalFilePath = FileUtil.getRealPath(context, thumbnailUri).path
                val thumbnailFile = File(thumbnailLocalFilePath)
                val thumbnailAWSFolderPath = UploadHelper.getConversationAttachmentFilePath(
                    conversation.chatroomId,
                    conversation.id,
                    attachment.type,
                    thumbnailFile,
                    isThumbnail = true
                )
                builder = builder.thumbnailUri(thumbnailUri)
                    .thumbnailLocalFilePath(thumbnailLocalFilePath)
                    .thumbnailAWSFolderPath(thumbnailAWSFolderPath)
            }

            builder.localFilePath(localFilePath).awsFolderPath(serverPath).build()
        }
    }

    fun deleteFailedConversation(conversationId: String) {
        val deleteConversationPermanentlyRequest = DeleteConversationPermanentlyRequest.Builder()
            .chatroomId(getChatroom()?.id ?: "")
            .conversationId(conversationId)
            .build()
        lmChatClient.deleteConversationPermanently(deleteConversationPermanentlyRequest)
    }

    fun updateChatroomWhileEditingTopic(
        conversation: ConversationViewData,
        text: String,
    ) {
        val updatedConversation = conversation.toBuilder()
            .answer(text)
            .isEdited(true)
            .build()

        chatroomDetail = chatroomDetail.toBuilder()
            .chatroom(
                getChatroom()?.toBuilder()
                    ?.topic(updatedConversation)
                    ?.build()
            )
            .build()
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
                _linkOgTags.postValue(null)
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
     * Triggers event when the current user tags someone
     * @param user User object
     * @param communityId Community id
     */
    fun sendUserTagEvent(user: TagViewData, communityId: String?) {
        LMAnalytics.track(
            LMAnalytics.Events.USER_TAGS_SOMEONE,
            mapOf(
                LMAnalytics.Keys.CHATROOM_NAME to getChatroom()?.header,
                "tagged_user_id" to user.id.toString(),
                "tagged_user_name" to user.name,
                LMAnalytics.Keys.COMMUNITY_ID to communityId,
            )
        )
    }

    /**
     * Triggers when the current user opens the chatroom
     * @param extras Chatroom detail fragment extra bundle
     */
    fun sendViewEvent(extras: ChatroomDetailExtras?) {
        if (extras == null) {
            return
        }
        val chatroom = getChatroom()
        LMAnalytics.track(
            LMAnalytics.Events.CHAT_ROOM_OPENED,
            mapOf(
                LMAnalytics.Keys.CHATROOM_ID to chatroom?.id,
                LMAnalytics.Keys.CHATROOM_TYPE to chatroom?.getTypeName(),
                LMAnalytics.Keys.CHATROOM_NAME to chatroom?.header,
                LMAnalytics.Keys.COMMUNITY_ID to chatroom?.communityId,
                LMAnalytics.Keys.SOURCE to extras.source,
                "source_chatroom_id" to extras.sourceChatroomId,
                "source_community_id" to extras.sourceCommunityId,
                "link" to extras.sourceLinkOrRoute,
                "route" to extras.sourceLinkOrRoute,
            )
        )
        if (extras.openedFromLink == true) {
            LMAnalytics.track(
                LMAnalytics.Events.CHATROOM_LINK_CLICKED,
                mapOf(
                    LMAnalytics.Keys.CHATROOM_ID to chatroom?.id,
                    LMAnalytics.Keys.CHATROOM_TYPE to chatroom?.getTypeName(),
                    LMAnalytics.Keys.COMMUNITY_ID to chatroom?.communityId,
                    LMAnalytics.Keys.SOURCE to extras.source,
                    "source_chatroom_id" to extras.sourceChatroomId,
                    "source_community_id" to extras.sourceCommunityId,
                    "link" to extras.sourceLinkOrRoute
                )
            )
        }
    }

    /**
     * Triggers when the current user un-follows the current chatroom
     * @param source Source of the event
     */
    private fun sendChatroomUnfollowed(source: String) {
        getChatroom()?.let { chatroomViewData ->
            LMAnalytics.track(
                LMAnalytics.Events.CHAT_ROOM_UN_FOLLOWED,
                mapOf(
                    LMAnalytics.Keys.CHATROOM_ID to chatroomViewData.id,
                    LMAnalytics.Keys.COMMUNITY_ID to chatroomViewData.communityId,
                    LMAnalytics.Keys.SOURCE to source,
                )
            )
        }
    }

    /**
     * Triggers when the current user follows the current chatroom
     * @param source Source of the event
     */
    private fun sendChatroomFollowed(source: String) {
        getChatroom()?.let { chatroomViewData ->
            LMAnalytics.track(
                LMAnalytics.Events.CHAT_ROOM_FOLLOWED,
                mapOf(
                    LMAnalytics.Keys.CHATROOM_ID to chatroomViewData.id,
                    LMAnalytics.Keys.COMMUNITY_ID to chatroomViewData.communityId,
                    LMAnalytics.Keys.SOURCE to source,
                    "member_state" to MemberState.getMemberState(
                        chatroomViewData.state
                    )
                )
            )
        }
    }

    /**
     * Triggers when the current chatroom is muted or un-muted
     * @param isChatroomMuted Chatroom is muted or not
     */
    fun sendChatroomMuted(isChatroomMuted: Boolean) {
        LMAnalytics.track(
            if (isChatroomMuted) {
                LMAnalytics.Events.CHATROOM_MUTED
            } else {
                LMAnalytics.Events.CHATROOM_UNMUTED
            },
            mapOf(
                LMAnalytics.Keys.CHATROOM_NAME to getChatroom()?.header,
                LMAnalytics.Keys.COMMUNITY_ID to getChatroom()?.communityId
            )
        )
    }

    /**
     * Triggers when the user records a voice message
     **/
    fun sendVoiceNoteRecorded() {
        getChatroomViewData()?.let { chatroom ->
            LMAnalytics.track(
                LMAnalytics.Events.VOICE_NOTE_RECORDED,
                mapOf(
                    LMAnalytics.Keys.CHATROOM_ID to chatroom.id,
                    LMAnalytics.Keys.COMMUNITY_ID to chatroom.communityId,
                    LMAnalytics.Keys.CHATROOM_TYPE to chatroom.type.toString()
                )
            )
        }
    }

    /**
     * Triggers when the user previews a voice message
     **/
    fun sendVoiceNotePreviewed() {
        getChatroomViewData()?.let { chatroom ->
            LMAnalytics.track(
                LMAnalytics.Events.VOICE_NOTE_PREVIEWED,
                mapOf(
                    LMAnalytics.Keys.CHATROOM_ID to chatroom.id,
                    LMAnalytics.Keys.COMMUNITY_ID to chatroom.communityId,
                    LMAnalytics.Keys.CHATROOM_TYPE to chatroom.type.toString()
                )
            )
        }
    }

    /**
     * Triggers when the user removes a recorded voice message
     **/
    fun sendVoiceNoteCanceled() {
        getChatroomViewData()?.let { chatroom ->
            LMAnalytics.track(
                LMAnalytics.Events.VOICE_NOTE_CANCELED,
                mapOf(
                    LMAnalytics.Keys.CHATROOM_ID to chatroom.id,
                    LMAnalytics.Keys.COMMUNITY_ID to chatroom.communityId,
                    LMAnalytics.Keys.CHATROOM_TYPE to chatroom.type.toString()
                )
            )
        }
    }

    /**
     * Triggers when the user sends a voice message
     **/
    private fun sendVoiceNoteSent(conversationId: String?) {
        if (conversationId.isNullOrEmpty()) return
        getChatroomViewData()?.let { chatroom ->
            LMAnalytics.track(
                LMAnalytics.Events.VOICE_NOTE_SENT,
                mapOf(
                    LMAnalytics.Keys.CHATROOM_ID to chatroom.id,
                    LMAnalytics.Keys.COMMUNITY_ID to chatroom.communityId,
                    LMAnalytics.Keys.CHATROOM_TYPE to chatroom.type.toString(),
                    LMAnalytics.Keys.MESSAGE_ID to conversationId
                )
            )
        }
    }

    /**
     * Triggers when the user plays a voice message
     **/
    fun sendVoiceNotePlayed(conversationId: String) {
        getChatroomViewData()?.let { chatroom ->
            LMAnalytics.track(
                LMAnalytics.Events.VOICE_NOTE_PLAYED,
                mapOf(
                    LMAnalytics.Keys.CHATROOM_ID to chatroom.id,
                    LMAnalytics.Keys.COMMUNITY_ID to chatroom.communityId,
                    LMAnalytics.Keys.CHATROOM_TYPE to chatroom.type.toString(),
                    LMAnalytics.Keys.MESSAGE_ID to conversationId
                )
            )
        }
    }

    /**
     * Triggers when the user plays the audio message
     **/
    fun sendAudioPlayedEvent(messageId: String) {
        getChatroomViewData()?.let { chatroom ->
            LMAnalytics.track(
                LMAnalytics.Events.AUDIO_PLAYED,
                mapOf(
                    LMAnalytics.Keys.CHATROOM_ID to chatroom.id,
                    LMAnalytics.Keys.COMMUNITY_ID to chatroom.communityId,
                    LMAnalytics.Keys.MESSAGE_ID to messageId
                )
            )
        }
    }

    /**
     * Triggers when the user clicks on a link
     **/
    fun sendChatLinkClickedEvent(messageId: String?, url: String) {
        getChatroomViewData()?.let { chatroom ->
            LMAnalytics.track(
                LMAnalytics.Events.CHAT_LINK_CLICKED,
                mapOf(
                    LMAnalytics.Keys.CHATROOM_ID to chatroom.id,
                    LMAnalytics.Keys.COMMUNITY_ID to chatroom.communityId,
                    LMAnalytics.Keys.USER_ID to userPreferences.getMemberId(),
                    LMAnalytics.Keys.MESSAGE_ID to messageId,
                    "url" to url,
                    "type" to "link",
                )
            )
        }
    }

    /**
     * Triggers when the user deletes messages
     **/
    private fun sendMessageDeletedEvent(conversations: List<ConversationViewData>) {
        getChatroomViewData()?.let { chatroom ->
            LMAnalytics.track(
                LMAnalytics.Events.MESSAGE_DELETED,
                mapOf(
                    "type" to ChatroomUtil.getConversationType(conversations.firstOrNull()),
                    LMAnalytics.Keys.CHATROOM_ID to chatroom.id,
                    LMAnalytics.Keys.COMMUNITY_ID to chatroom.communityId,
                    "message_ids" to conversations.joinToString { it.id }
                )
            )
        }
    }

    /**
     * Triggers when the user copy messages
     **/
    fun sendMessageCopyEvent(message: String) {
        getChatroomViewData()?.let { chatroom ->
            LMAnalytics.track(
                LMAnalytics.Events.MESSAGE_COPIED,
                mapOf(
                    LMAnalytics.Keys.CHATROOM_ID to chatroom.id,
                    LMAnalytics.Keys.COMMUNITY_ID to chatroom.communityId,
                    "messages" to message
                )
            )
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
        LMAnalytics.track(
            LMAnalytics.Events.SET_CHATROOM_TOPIC,
            mapOf(
                LMAnalytics.Keys.CHATROOM_ID to chatroomId,
                "conversationId" to conversationId,
                "topic" to topic
            )
        )
    }

    /**
     * Triggers when the current user leave the current chatroom
     */
    private fun sendChatRoomLeftEvent() {
        LMAnalytics.track(
            LMAnalytics.Events.CHAT_ROOM_LEFT,
            mapOf(
                LMAnalytics.Keys.CHATROOM_ID to chatroomDetail.chatroom?.id,
                LMAnalytics.Keys.COMMUNITY_ID to chatroomDetail.chatroom?.communityId,
                LMAnalytics.Keys.CHATROOM_NAME to chatroomDetail.chatroom?.header,
                LMAnalytics.Keys.CHATROOM_TYPE to chatroomDetail.chatroom?.getTypeName(),
                "chatroom_category" to "secret",
            )
        )
    }

    /**
     * Triggers when the current user starts sharing the chatroom
     */
    fun sendChatroomShared() {
        LMAnalytics.track(
            LMAnalytics.Events.CHAT_ROOM_SHARED,
            mapOf(
                LMAnalytics.Keys.CHATROOM_ID to getChatroom()?.id,
                LMAnalytics.Keys.COMMUNITY_ID to getChatroom()?.communityId,
                LMAnalytics.Keys.CHATROOM_NAME to getChatroom()?.header,
                LMAnalytics.Keys.COMMUNITY_NAME to getChatroom()?.communityName,
            )
        )
    }

    /**
     * Triggers when the user edits a message
     **/
    private fun sendMessageEditedEvent(conversation: ConversationViewData?) {
        getChatroomViewData()?.let { chatroom ->
            LMAnalytics.track(
                LMAnalytics.Events.MESSAGE_EDITED,
                mapOf(
                    LMAnalytics.Keys.CHATROOM_ID to chatroom.id,
                    LMAnalytics.Keys.COMMUNITY_ID to chatroom.communityId,
                    LMAnalytics.Keys.MESSAGE_ID to conversation?.id
                )
            )
        }
    }

    /**
     * Triggers when the user replies to message
     **/
    private fun sendMessageReplyEvent(
        conversation: ConversationViewData?,
        repliedMemberId: String?,
        repliedMemberState: String?,
        repliedMessageId: String?,
        type: String?
    ) {
        getChatroomViewData()?.let { chatroom ->
            LMAnalytics.track(
                LMAnalytics.Events.MESSAGE_REPLY,
                mapOf(
                    "message_type" to ChatroomUtil.getConversationType(conversation),
                    LMAnalytics.Keys.CHATROOM_ID to chatroom.id,
                    LMAnalytics.Keys.COMMUNITY_ID to chatroom.communityId,
                    "replied_to_member_id" to repliedMemberId,
                    "replied_to_member_state" to repliedMemberState,
                    "replied_to_message_id" to repliedMessageId,
                    "type" to type
                )
            )
        }
    }

    /**
     * Triggers when the current user sends a conversation in the current chatroom
     * @param taggedUsers List of all the tagged users present in the sent conversation
     * @param conversation Conversation object
     */
    private fun sendChatroomResponded(
        taggedUsers: List<TagViewData>,
        conversation: ConversationViewData?,
    ) {
        if (conversation == null) {
            return
        }
        LMAnalytics.track(
            LMAnalytics.Events.CHATROOM_RESPONDED,
            mapOf(
                LMAnalytics.Keys.CHATROOM_NAME to getChatroom()?.header,
                LMAnalytics.Keys.CHATROOM_TYPE to getChatroom()?.getTypeName(),
                LMAnalytics.Keys.COMMUNITY_ID to getChatroom()?.communityId,
                LMAnalytics.Keys.SOURCE to "chatroom",
                "message_type" to ChatroomUtil.getConversationType(conversation),
                "count_tagged_user" to taggedUsers.size.toString(),
                "name_tagged_user" to taggedUsers.joinToString { it.name },
                "member_state" to MemberState.getMemberState(currentMemberDataFromMemberState?.state),
            )
        )
    }

    /**
     * Triggers when the current user opens the emoji keyboard view
     */
    fun sendReactionsClickEvent() {
        getChatroomViewData()?.id?.let { chatroomId ->
            LMAnalytics.track(
                LMAnalytics.Events.REACTIONS_CLICKED,
                mapOf(
                    LMAnalytics.Keys.CHATROOM_ID to chatroomId,
                    LMAnalytics.Keys.SOURCE to "chatroom"
                )
            )
        }
    }
}