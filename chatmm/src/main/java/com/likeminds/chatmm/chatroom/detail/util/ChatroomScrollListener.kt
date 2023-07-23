package com.likeminds.chatmm.chatroom.detail.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.chatmm.chatroom.detail.model.SCROLL_DOWN
import com.likeminds.chatmm.chatroom.detail.model.SCROLL_UP
import com.likeminds.chatmm.chatroom.detail.model.ScrollState
import com.likeminds.chatmm.chatroom.detail.viewmodel.ChatroomDetailViewModel

/**
 * Scroll listener for chatroom detail screen
 * @param mLinearLayoutManager Layout manager of chatroom recyclerview
 */
abstract class ChatroomScrollListener(
    private val mLinearLayoutManager: LinearLayoutManager,
) : RecyclerView.OnScrollListener() {

    companion object {
        //Offset for pagination to trigger, try to keep it 20% of paged size for better visual
        private const val OFFSET = ChatroomDetailViewModel.CONVERSATIONS_LIMIT * 0.2
    }

    // Total number of items currently present in the data set
    private var totalItemCount = 0

    // True if loading is done at top
    private var topLoading = true

    // True if loading is done at bottom
    private var bottomLoading = true

    // The position of the chat room in the recyclerview. Pass -1 to reset it
    private var chatRoomPosition = -1

    // The visibility of the chat room type main view in the list
    private var chatRoomViewVisible = false

    fun setChatRoomPosition(chatRoomPosition: Int) {
        this.chatRoomPosition = chatRoomPosition
    }

    fun containsChatRoom(): Boolean {
        return chatRoomPosition >= 0 && chatRoomPosition < mLinearLayoutManager.itemCount
    }

    fun getChatroomPosition() = chatRoomPosition

    fun shouldShowTopChatRoom(): Boolean {
        val firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition()
        if (chatRoomPosition == -1 || chatRoomPosition !in firstVisibleItemPosition..lastVisibleItemPosition) {
            return true
        }
        return false
    }

    /**
     * Call this once you are done adding items at top position of the recyclerview
     */
    fun topLoadingDone() {
        topLoading = true
    }

    /**
     * Call this once you are done adding items at bottom position of the recyclerview
     */
    fun bottomLoadingDone() {
        bottomLoading = true
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy == 0) {
            return
        }

        onScroll()
        totalItemCount = mLinearLayoutManager.itemCount
        val firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition()

        /**
         * For pagination trigger
         */
        if (dy > 0) {
            /**
             * Scroll down
             */
            onScrollingToBottom(mLinearLayoutManager.findLastCompletelyVisibleItemPosition())
            if (bottomLoading) {
                if (lastVisibleItemPosition > (totalItemCount - OFFSET)) {
                    bottomLoading = false
                    onLoadMore(SCROLL_DOWN)
                }
            }
        } else {
            /**
             * Scroll up
             */
            if (lastVisibleItemPosition < totalItemCount - 1) {
                onScrollingToTop()
            }
            if (topLoading) {
                if (firstVisibleItemPosition < OFFSET) {
                    topLoading = false
                    onLoadMore(SCROLL_UP)
                }
            }
        }

        /**
         * Check if chat room header is visible on screen
         * If yes, hide the chatroom top bar else show it
         */
        if (chatRoomPosition in firstVisibleItemPosition..lastVisibleItemPosition) {
            if (chatRoomViewVisible) {
                onChatRoomVisibilityChanged(false)
                chatRoomViewVisible = false
            }
        } else {
            if (!chatRoomViewVisible) {
                onChatRoomVisibilityChanged(true)
                chatRoomViewVisible = true
            }
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (!recyclerView.canScrollVertically(1)) {
            onBottomReached()
        }
    }

    abstract fun onLoadMore(@ScrollState scrollState: Int)

    abstract fun onChatRoomVisibilityChanged(show: Boolean)

    abstract fun onBottomReached()

    abstract fun onScrollingToTop()

    abstract fun onScrollingToBottom(lastItemPosition: Int)

    abstract fun onScroll()
}