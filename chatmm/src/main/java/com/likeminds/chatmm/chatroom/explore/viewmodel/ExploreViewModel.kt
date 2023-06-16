package com.likeminds.chatmm.chatroom.explore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.explore.model.ExploreViewData
import com.likeminds.chatmm.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.chatroom.model.FollowChatroomRequest
import com.likeminds.likemindschat.community.model.GetExploreFeedRequest
import com.likeminds.likemindschat.community.model.GetExploreFeedResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class ExploreViewModel @Inject constructor(
    private val sdkPreferences: SDKPreferences
) : ViewModel() {

    companion object {
        private const val TAG = "ExploreVM"

        //Menu
        private const val MENU_NEWEST = "Newest"
        private const val MENU_RECENTLY_ACTIVE = "Recently active"
        private const val MENU_MOST_PARTICIPANTS = "Most participants"
        private const val MENU_MOST_MESSAGES = "Most messages"
    }

    private val lmChatClient = LMChatClient.getInstance()

    private val _menuActions: MutableLiveData<List<OverflowMenuItemViewData>> = MutableLiveData()
    val menuActions: LiveData<List<OverflowMenuItemViewData>> = _menuActions

    private val _selectedOrder: MutableLiveData<String> = MutableLiveData(MENU_NEWEST)
    val selectedOrder: LiveData<String> = _selectedOrder

    //First -> ViewData and Second -> Position to be updated
    private val _followStatus = MutableLiveData<Pair<ExploreViewData, Int>>()
    val followStatus: LiveData<Pair<ExploreViewData, Int>> = _followStatus

    private val _showPinnedIcon = MutableLiveData<Boolean>()
    val showPinnedIcon: LiveData<Boolean> = _showPinnedIcon

    private val _dataList = MutableLiveData<ArrayList<ExploreViewData>>()
    val dataList: LiveData<ArrayList<ExploreViewData>> = _dataList

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageFlow = errorEventChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class ExploreFeed(val errorMessage: String?) : ErrorMessageEvent()
        data class ChatroomFollow(val errorMessage: String?) : ErrorMessageEvent()
    }

    var showPinnedChatroomOnly: Boolean = false

    init {
        setMenuItems()
    }

    private fun setMenuItems() {
        val menu = mutableListOf<OverflowMenuItemViewData>()
        menu.add(OverflowMenuItemViewData.Builder().title(MENU_NEWEST).build())
        menu.add(OverflowMenuItemViewData.Builder().title(MENU_RECENTLY_ACTIVE).build())
        menu.add(OverflowMenuItemViewData.Builder().title(MENU_MOST_PARTICIPANTS).build())
        menu.add(OverflowMenuItemViewData.Builder().title(MENU_MOST_MESSAGES).build())
        _menuActions.postValue(menu)
    }

    fun setSelectedOrder(order: String) {
        _selectedOrder.value = order
    }

    fun setShowPinnedChatroomsOnly(value: Boolean) {
        showPinnedChatroomOnly = value
    }

    fun getInitialExploreFeed() {
        if (showPinnedChatroomOnly) {
            getExploreFeed(1, true)
        } else {
            getExploreFeed(1)
        }
    }

    fun getExploreFeed(page: Int, isPinned: Boolean? = null) {
        viewModelScope.launchIO {
            val request = GetExploreFeedRequest.Builder()
                .orderType(getOrderType())
                .isPinned(isPinned)
                .page(page)
                .build()

            val getExploreFeedResponse = lmChatClient.getExploreFeed(request)

            if (getExploreFeedResponse.success) {
                val data = getExploreFeedResponse.data
                if (data != null) {
                    handleExploreFeedResponse(data, page)
                }
            } else {
                val errorMessage = getExploreFeedResponse.errorMessage
                Log.e(TAG, "explore feed failed: $errorMessage")
                errorEventChannel.send(ErrorMessageEvent.ExploreFeed(errorMessage))
            }
        }
    }

    private fun getOrderType(): Int {
        return when (_selectedOrder.value) {
            MENU_NEWEST -> 0
            MENU_RECENTLY_ACTIVE -> 1
            MENU_MOST_PARTICIPANTS -> 3
            MENU_MOST_MESSAGES -> 2
            else -> 0
        }
    }

    private fun handleExploreFeedResponse(
        exploreFeedData: GetExploreFeedResponse,
        page: Int
    ) {
        val baseViewTypeList: ArrayList<ExploreViewData> = arrayListOf()

        if (page == 1 && exploreFeedData.pinnedChatroomCount > 3 && !showPinnedChatroomOnly) {
            _showPinnedIcon.postValue(true)
        }

        exploreFeedData.chatrooms.forEachIndexed { index, chatroom ->
            val sortIndex = ((page * 10) + index)
            ViewDataConverter.convertChatroom(
                chatroom,
                sdkPreferences.getMemberId(),
                sortIndex
            )?.apply {
                baseViewTypeList.add(this)
            }
        }

        _dataList.postValue(baseViewTypeList)
    }

    fun followChatroom(
        follow: Boolean,
        exploreViewData: ExploreViewData,
        position: Int
    ) {
        viewModelScope.launchIO {
            val request = FollowChatroomRequest.Builder()
                .chatroomId(exploreViewData.id)
                .memberId(sdkPreferences.getMemberId())
                .value(follow)
                .build()

            val followChatroomResponse = lmChatClient.followChatroom(request)

            if (followChatroomResponse.success) {
                if (follow) {
                    sendChatRoomFollowed(exploreViewData.chatroomViewData)
                } else {
                    sendChatRoomUnFollowed(exploreViewData.chatroomViewData)
                }

                _followStatus.postValue(
                    Pair(
                        exploreViewData.toBuilder().followStatus(follow).build(),
                        position
                    )
                )
            } else {
                val errorMessage = followChatroomResponse.errorMessage
                Log.e(
                    TAG,
                    "chatroom/follow failed, $errorMessage"
                )
                errorEventChannel.send(ErrorMessageEvent.ChatroomFollow(errorMessage))
            }
        }
    }

    // todo: analytics
    private fun sendChatRoomUnFollowed(chatroomViewData: ChatroomViewData??) {
        if (chatroomViewData == null) return
//        LMAnalytics.track(
//            LMAnalytics.Keys.EVENT_CHAT_ROOM_UN_FOLLOWED,
//            JSONObject().apply {
//                put("chatroom_id", chatroomViewData.id)
//                put("community_id", chatroomViewData.communityId)
//                put("source", LMAnalytics.Sources.SOURCE_COMMUNITY_FEED)
//            }
//        )
    }

    private fun sendChatRoomFollowed(chatroomViewData: ChatroomViewData?) {
        if (chatroomViewData == null) return
//        LMAnalytics.track(
//            LMAnalytics.Keys.EVENT_CHAT_ROOM_FOLLOWED,
//            JSONObject().apply {
//                put("chatroom_id", chatroomViewData.id)
//                put("community_id", chatroomViewData.communityId)
//                put("source", LMAnalytics.Sources.SOURCE_COMMUNITY_FEED)
//            }
//        )
    }
}