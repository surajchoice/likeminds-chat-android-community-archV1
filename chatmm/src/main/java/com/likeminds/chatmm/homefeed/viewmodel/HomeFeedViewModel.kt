package com.likeminds.chatmm.homefeed.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import androidx.work.WorkInfo
import com.likeminds.chatmm.*
import com.likeminds.chatmm.homefeed.model.*
import com.likeminds.chatmm.homefeed.util.HomeFeedPreferences
import com.likeminds.chatmm.homefeed.util.HomeFeedUtil
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.utils.ValueUtils.isValidIndex
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.chatroom.model.Chatroom
import com.likeminds.likemindschat.homefeed.util.HomeChatroomListener
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeFeedViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val homeFeedPreferences: HomeFeedPreferences,
) : ViewModel() {
    companion object {
        private const val TAG = "Home VM"
    }

    private val lmChatClient = LMChatClient.getInstance()

    private val compositeDisposable = CompositeDisposable()

    private val _userData = MutableLiveData<MemberViewData?>()
    val userData: LiveData<MemberViewData?> = _userData

    private val errorMessageChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageEventFlow = errorMessageChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class GetChatroom(val errorMessage: String?) : ErrorMessageEvent()

        data class GetExploreTabCount(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val chatroomListener = object : HomeChatroomListener() {
        override fun initial(chatrooms: List<Chatroom>) {
            if (chatrooms.isNotEmpty()) {
                setWasChatroomFetched(true)
            }
            showInitialChatrooms(chatrooms)
        }

        override fun onChanged(
            removedIndex: List<Int>,
            inserted: List<Pair<Int, Chatroom>>,
            changed: List<Pair<Int, Chatroom>>
        ) {
            if (inserted.isNotEmpty() || changed.isNotEmpty()) {
                setWasChatroomFetched(true)
            }
            updateChatroomChanges(
                removedIndex,
                inserted,
                changed
            )
        }

        override fun onError(throwable: Throwable) {
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
        return (lmChatClient.getDBEmpty().data?.isDBEmpty ?: false)
    }

    fun getUserFromLocalDb() {
        val userResponse = lmChatClient.getLoggedInUser()
        _userData.postValue(ViewDataConverter.convertUser(userResponse.data?.user))
    }

    fun refetchChatrooms() {
        compositeDisposable.clear()
        observeChatrooms()
    }

    fun observeChatrooms() {
        compositeDisposable.clear()
        val disposable = lmChatClient.getChatrooms(
            chatroomListener,
        )?.subscribeOn(Schedulers.computation())
            ?.observeOn(Schedulers.newThread())
            ?.subscribe() ?: return
        compositeDisposable.add(disposable)
    }

    private fun showInitialChatrooms(
        chatrooms: List<Chatroom?>,
    ) = viewModelScope.launch {
        val chatViewDataList = chatrooms.map { chatroom ->
            if (chatroom == null) return@launch
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
        dataList.add(HomeFeedUtil.getContentHeaderView(context.getString(R.string.lm_chat_joined_chatrooms)))
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

    fun syncChatrooms(context: Context): Pair<LiveData<MutableList<WorkInfo>>?, LiveData<MutableList<WorkInfo>>?>? {
        return lmChatClient.syncChatrooms(context)
    }

    fun observeLiveHomeFeed(context: Context) {
        lmChatClient.observeLiveHomeFeed(context)
    }

    fun removeLiveHomeFeedListener() {
        lmChatClient.removeLiveHomeFeedListener()
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

    override fun onCleared() {
        chatroomListener.clear()
        compositeDisposable.dispose()
        super.onCleared()
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

    //When sync is complete for the user for normal as well as guest
    fun sendSyncCompleteEvent(timeTaken: Float) {
        LMAnalytics.track(
            LMAnalytics.Events.SYNC_COMPLETE,
            mapOf(
                "time_taken_to_complete" to "$timeTaken secs"
            )
        )
    }
}