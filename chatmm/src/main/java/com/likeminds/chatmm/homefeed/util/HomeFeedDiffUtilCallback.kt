package com.likeminds.chatmm.homefeed.util

import androidx.recyclerview.widget.DiffUtil
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.homefeed.model.*
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.utils.model.BaseViewType

internal class HomeFeedDiffUtilCallback(
    private val oldList: List<BaseViewType>,
    private val newList: List<BaseViewType>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when {
            oldItem is HomeFeedItemViewData && newItem is HomeFeedItemViewData -> {
                oldItem.chatroom.id == newItem.chatroom.id
            }
            oldItem is HomeLineBreakViewData && newItem is HomeLineBreakViewData -> true
            oldItem is HomeBlankSpaceViewData && newItem is HomeBlankSpaceViewData -> true
            oldItem is ContentHeaderViewData && newItem is ContentHeaderViewData -> {
                oldItem.title == newItem.title
            }
            oldItem is EmptyScreenViewData && newItem is EmptyScreenViewData -> true
            oldItem is HomeChatroomListShimmerViewData && newItem is HomeChatroomListShimmerViewData -> true
            oldItem is HomeFeedViewData && newItem is HomeFeedViewData -> true
            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when {
            oldItem is HomeFeedItemViewData && newItem is HomeFeedItemViewData -> {
                chatViewData(oldItem, newItem)
            }
            oldItem is ContentHeaderViewData && newItem is ContentHeaderViewData -> {
                oldItem.title == newItem.title
            }
            oldItem is EmptyScreenViewData && newItem is EmptyScreenViewData -> true
//            oldItem is ProgressViewData && newItem is ProgressViewData -> true
//            oldItem is ProgressHorizontalViewData && newItem is ProgressHorizontalViewData -> true
//            oldItem is ChatroomListShimmerViewData && newItem is ChatroomListShimmerViewData -> true
            oldItem is HomeFeedViewData && newItem is HomeFeedViewData -> {
                homeFeedViewData(oldItem, newItem)
            }
            else -> false
        }
    }

    /**
     * Check the differences in only those data which are shown to the UI.
     */
    private fun chatViewData(
        oldItem: HomeFeedItemViewData,
        newItem: HomeFeedItemViewData
    ): Boolean {
        return oldItem.chatroom.header == newItem.chatroom.header
                && oldItem.chatroom.title == newItem.chatroom.title
                && oldItem.lastConversationTime == newItem.lastConversationTime
                && oldItem.unseenConversationCount == newItem.unseenConversationCount
                && oldItem.chatroom.isTagged == newItem.chatroom.isTagged
                && oldItem.chatroom.muteStatus == newItem.chatroom.muteStatus
                && oldItem.chatroom.type == newItem.chatroom.type
                && oldItem.chatroom.isSecret == newItem.chatroom.isSecret
                && oldItem.chatroomImageUrl == newItem.chatroomImageUrl
                && oldItem.isLastItem == newItem.isLastItem
                && memberViewData(oldItem.chatroom.memberViewData, newItem.chatroom.memberViewData)
                && conversationViewData(oldItem.lastConversation, newItem.lastConversation)
    }

    private fun conversationViewData(
        oldItem: ConversationViewData?,
        newItem: ConversationViewData?
    ): Boolean {
        return oldItem?.id == newItem?.id
                && oldItem?.answer == newItem?.answer
                && oldItem?.ogTags?.title == newItem?.ogTags?.title
                && oldItem?.date == newItem?.date
                && oldItem?.deletedBy == newItem?.deletedBy
                && oldItem?.attachments?.size == newItem?.attachments?.size
                && oldItem?.attachmentsUploaded == newItem?.attachmentsUploaded
                && oldItem?.attachmentCount == newItem?.attachmentCount
    }

    private fun memberViewData(oldItem: MemberViewData?, newItem: MemberViewData?): Boolean {
        return oldItem?.imageUrl == newItem?.imageUrl && oldItem?.name == newItem?.name
    }

    private fun memberViewDataList(
        oldItem: List<MemberViewData>?,
        newItem: List<MemberViewData>?
    ): Boolean {
        if (oldItem.isNullOrEmpty() && newItem.isNullOrEmpty()) {
            return true
        }
        if (oldItem?.size != newItem?.size) {
            return false
        }
        for (i in oldItem!!.indices) {
            if (!memberViewData(oldItem[i], newItem!![i])) {
                return false
            }
        }
        return true
    }

    private fun homeFeedViewData(
        oldItem: HomeFeedViewData?,
        newItem: HomeFeedViewData?
    ): Boolean {
        if (oldItem == null && newItem == null) {
            return true
        }
        if (oldItem == null || newItem == null) {
            return false
        }
        return oldItem.totalChatRooms == newItem.totalChatRooms
                && oldItem.newChatRooms == newItem.newChatRooms
    }

}