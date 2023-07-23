package com.likeminds.chatmm.homefeed.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import com.likeminds.chatmm.*
import com.likeminds.chatmm.chatroom.detail.util.ChatroomUtil
import com.likeminds.chatmm.homefeed.model.*
import com.likeminds.chatmm.homefeed.util.HomeFeedPreferences
import com.likeminds.chatmm.homefeed.util.HomeFeedUtil
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.MemberUtil
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.utils.*
import com.likeminds.chatmm.utils.ValueUtils.isValidIndex
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.chatmm.utils.coroutine.launchMain
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_HOME_CHAT_ROOM
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.chatroom.model.Chatroom
import com.likeminds.likemindschat.homefeed.util.HomeFeedChangeListener
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeFeedViewModel @Inject constructor(
    private val sdkPreferences: SDKPreferences,
    private val userPreferences: UserPreferences,
    private val homeFeedPreferences: HomeFeedPreferences,
) : ViewModel() {
    companion object {
        private const val TAG = "Home VM"
    }

    private val lmChatClient = LMChatClient.getInstance()

    private val _userData = MutableLiveData<MemberViewData?>()
    val userData: LiveData<MemberViewData?> = _userData

    private val errorMessageChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageEventFlow = errorMessageChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class GetChatroom(val errorMessage: String?) : ErrorMessageEvent()
        data class GetExploreTabCount(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val chatroomListener = object : HomeFeedChangeListener {
        override fun initialChatrooms(chatrooms: List<Chatroom>) {
            super.initialChatrooms(chatrooms)
            if (chatrooms.isNotEmpty()) {
                setWasChatroomFetched(true)
            }
            showInitialChatrooms(chatrooms)
        }

        override fun changedChatrooms(
            removedIndex: List<Int>,
            inserted: List<Pair<Int, Chatroom>>,
            changed: List<Pair<Int, Chatroom>>
        ) {
            super.changedChatrooms(
                removedIndex,
                inserted,
                changed
            )
            if (inserted.isNotEmpty() || changed.isNotEmpty()) {
                setWasChatroomFetched(true)
            }
            updateChatroomChanges(
                removedIndex,
                inserted,
                changed
            )
        }

        override fun error(throwable: Throwable) {
            super.error(throwable)
            viewModelScope.launchIO {
                errorMessageChannel.send(ErrorMessageEvent.GetChatroom(throwable.message))
            }
            Log.e(TAG, "HomeFeedChangeListener", throwable)
        }
    }

    private val chatRoomListShimmerView by lazy {
        HomeChatroomListShimmerViewData.Builder().build()
    }

    private val lineBreakViewData by lazy { HomeLineBreakViewData.Builder().build() }

    private val blankSpaceViewData by lazy { HomeBlankSpaceViewData.Builder().build() }

    sealed class HomeEvent {
        object UpdateChatrooms : HomeEvent()
    }

    private val homeEventChannel = Channel<HomeEvent>(Channel.BUFFERED)
    val homeEventsFlow = homeEventChannel.receiveAsFlow()

    private val allChatRoomsData = mutableListOf<HomeFeedItemViewData>()

    private var totalChatroomCount: Int = 0
    private var unseenChatroomCount: Int = 0

    fun setWasChatroomFetched(value: Boolean) {
        homeFeedPreferences.setShowHomeFeedShimmer(value)
    }

    fun isDBEmpty(): Boolean {
        return (lmChatClient.getDBEmpty().data?.isDBEmpty ?: true)
    }

    fun getUserFromLocalDb() {
        val userResponse = lmChatClient.getUser()
        _userData.postValue(ViewDataConverter.convertUser(userResponse.data?.user))
    }

    fun observeChatrooms(context: Context) {
        viewModelScope.launchMain {
            lmChatClient.getChatrooms(context, chatroomListener)
        }
    }

    private fun showInitialChatrooms(
        chatrooms: List<Chatroom>,
    ) = viewModelScope.launch {
        val chatViewDataList = chatrooms.map { chatroom ->
            HomeFeedUtil.getChatRoomViewData(chatroom, userPreferences)
        }
        allChatRoomsData.clear()
        allChatRoomsData.addAll(chatViewDataList)
        homeEventChannel.send(HomeEvent.UpdateChatrooms)
    }

    private fun updateChatroomChanges(
        removedId: List<Int>,
        inserted: List<Pair<Int, Chatroom>>,
        changed: List<Pair<Int, Chatroom>>,
    ) = viewModelScope.launch {
        try {
            removedId.forEach { index ->
                if (index.isValidIndex(allChatRoomsData)) {
                    allChatRoomsData.removeAt(index)
                }
            }
            val insertedChatViewDataList = inserted.map { pair ->
                Pair(pair.first, HomeFeedUtil.getChatRoomViewData(pair.second, userPreferences))
            }
            val changedChatViewDataList = changed.map { pair ->
                Pair(pair.first, HomeFeedUtil.getChatRoomViewData(pair.second, userPreferences))
            }

            insertedChatViewDataList.forEach { pair ->
                val index = pair.first
                val chatViewData = pair.second
                allChatRoomsData.add(index, chatViewData)
            }

            changedChatViewDataList.forEach { pair ->
                val index = pair.first
                val chatViewData = pair.second
                if (index.isValidIndex(allChatRoomsData)) {
                    allChatRoomsData[index] = chatViewData
                }
            }
            homeEventChannel.send(HomeEvent.UpdateChatrooms)
        } catch (e: Exception) {
            Log.e(SDKApplication.LOG_TAG, "updateChatroomChanges ${e.localizedMessage}")
        }
    }

    private fun getChatRoomViewData(chatroom: Chatroom): HomeFeedItemViewData {
        val chatroomViewData =
            ViewDataConverter.convertChatroomForHome(chatroom, ITEM_HOME_CHAT_ROOM)
        val lastConversation =
            ViewDataConverter.convertConversation(chatroom.lastConversation)

        val lastConversationMemberName = MemberUtil.getFirstNameToShow(
            userPreferences,
            lastConversation?.memberViewData
        )
        val lastConversationText = ChatroomUtil.getLastConversationTextForHome(lastConversation)
        val lastConversationTime = if (chatroom.isDraft == true) {
            chatroomViewData.cardCreationTime ?: ""
        } else {
            TimeUtil.getLastConversationTime(chatroomViewData.updatedAt)
        }
        return HomeFeedItemViewData.Builder()
            .chatroom(chatroomViewData)
            .lastConversation(lastConversation)
            .lastConversationTime(lastConversationTime)
            .unseenConversationCount(chatroomViewData.unseenCount ?: 0)
            .chatTypeDrawableId(ChatroomUtil.getTypeDrawableId(chatroomViewData.type))
            .lastConversationText(lastConversationText)
            .lastConversationMemberName(lastConversationMemberName)
            .isLastItem(true)
            .chatroomImageUrl(chatroomViewData.chatroomImageUrl)
            .build()
    }

    fun getHomeFeedList(context: Context): List<BaseViewType> {
        val dataList = mutableListOf<BaseViewType>()

        dataList.add(
            HomeFeedViewData.Builder()
                .totalChatRooms(totalChatroomCount)
                .newChatRooms(unseenChatroomCount)
                .build()
        )

        dataList.add(lineBreakViewData)

        //Chat rooms
        dataList.add(HomeFeedUtil.getContentHeaderView(context.getString(R.string.joined_chatrooms)))
        val wasChatroomsFetched = homeFeedPreferences.getShowHomeFeedShimmer()
        when {
            !wasChatroomsFetched -> {
                dataList.add(chatRoomListShimmerView)
            }

            allChatRoomsData.isNotEmpty() -> {
                val lastItem = allChatRoomsData.lastOrNull()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    allChatRoomsData.replaceAll { chatViewData ->
                        chatViewData.toBuilder()
                            .isLastItem(lastItem?.chatroom?.id == chatViewData.chatroom.id)
                            .build()
                    }
                } else {
                    val updatedAllChatRoomsData = allChatRoomsData.map { chatViewData ->
                        chatViewData.toBuilder()
                            .isLastItem(lastItem?.chatroom?.id == chatViewData.chatroom.id)
                            .build()
                    }
                    allChatRoomsData.clear()
                    allChatRoomsData.addAll(updatedAllChatRoomsData)
                }
                dataList.addAll(allChatRoomsData)
            }

            else -> {
                dataList.add(HomeFeedUtil.getEmptyChatView(context))
            }
        }
        //To add padding at bottom to show FAB button clearly
        dataList.add(blankSpaceViewData)
        return dataList
    }

    fun getConfig() {
        viewModelScope.launchIO {
            val getConfigResponse = lmChatClient.getConfig()

            if (getConfigResponse.success) {
                val data = getConfigResponse.data
                if (data != null) {
                    sdkPreferences.setMicroPollsEnabled(data.enableMicroPolls)
                    sdkPreferences.setGifSupportEnabled(data.enableGifs)
                    sdkPreferences.setAudioSupportEnabled(data.enableAudio)
                    sdkPreferences.setVoiceNoteSupportEnabled(data.enableVoiceNote)

                    // todo:
//                    LMAnalytics.setSentryUserData(member)
                }
            } else {
                Log.d(
                    SDKApplication.LOG_TAG,
                    "config api failed: ${getConfigResponse.errorMessage}"
                )
                // sets default values to config prefs
                sdkPreferences.setDefaultConfigPrefs()
            }
        }
    }

    fun getExploreTabCount() {
        viewModelScope.launchIO {
            val getExploreTabCountResponse = lmChatClient.getExploreTabCount()

            if (getExploreTabCountResponse.success) {
                val data = getExploreTabCountResponse.data
                if (data != null) {
                    this.totalChatroomCount = data.totalChatroomCount
                    this.unseenChatroomCount = data.unseenChatroomCount
                    homeEventChannel.send(HomeEvent.UpdateChatrooms)
                }
            } else {
                // send error
                val errorMessage = getExploreTabCountResponse.errorMessage
                errorMessageChannel.send(
                    ErrorMessageEvent.GetExploreTabCount(errorMessage)
                )
                Log.e(SDKApplication.LOG_TAG, "$errorMessage")
            }
        }
    }

    /**------------------------------------------------------------
     * Analytics events
    ---------------------------------------------------------------*/

    //When a user clicks on the community tab
    fun sendCommunityTabClicked(communityId: String?, communityName: String?) {
        LMAnalytics.track(
            LMAnalytics.Events.COMMUNITY_TAB_CLICKED,
            mapOf(
                LMAnalytics.Keys.COMMUNITY_NAME to communityName,
                LMAnalytics.Keys.COMMUNITY_ID to communityId,
            )
        )
    }

    //When a user clicks on the community tab
    fun sendCommunityFeedClickedEvent(communityId: String, communityName: String) {
        LMAnalytics.track(
            LMAnalytics.Events.COMMUNITY_FEED_CLICKED,
            mapOf(
                LMAnalytics.Keys.COMMUNITY_NAME to communityName,
                LMAnalytics.Keys.COMMUNITY_ID to communityId,
            )
        )
    }

    // when home screen is opened
    fun sendHomeScreenOpenedEvent(source: String?) {
        if (!source.isNullOrEmpty()) {
            LMAnalytics.track(
                LMAnalytics.Events.HOME_FEED_PAGE_OPENED,
                mapOf(
                    LMAnalytics.Keys.SOURCE to source,
                )
            )
        }
    }
}