package com.likeminds.chatmm.homefeed.util

import androidx.recyclerview.widget.DiffUtil
import com.likeminds.chatmm.chatroom.detail.model.MemberViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.homefeed.model.*
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
            oldItem is ChatViewData && newItem is ChatViewData -> {
                oldItem.chatroom.id == newItem.chatroom.id
            }
            oldItem is HomeLineBreakViewData && newItem is HomeLineBreakViewData -> true
            oldItem is HomeBlankSpaceViewData && newItem is HomeBlankSpaceViewData -> true
            oldItem is ContentHeaderViewData && newItem is ContentHeaderViewData -> {
                oldItem.title == newItem.title
            }
            oldItem is EmptyScreenViewData && newItem is EmptyScreenViewData -> true
            oldItem is ChatroomListShimmerViewData && newItem is ChatroomListShimmerViewData -> true
            oldItem is HomeFeedViewData && newItem is HomeFeedViewData -> true
            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when {
            oldItem is ChatViewData && newItem is ChatViewData -> {
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
    private fun chatViewData(oldItem: ChatViewData, newItem: ChatViewData): Boolean {
        return oldItem.chatroom.header == newItem.chatroom.header
                && oldItem.chatroom.title == newItem.chatroom.title
                && oldItem.isDraft == newItem.isDraft
                && oldItem.lastConversationTime == newItem.lastConversationTime
                && oldItem.unseenConversationCount == newItem.unseenConversationCount
                && oldItem.chatroom.isTagged == newItem.chatroom.isTagged
                && oldItem.chatroom.muteStatus == newItem.chatroom.muteStatus
                && oldItem.chatroom.type == newItem.chatroom.type
                && oldItem.chatroom.isSecret == newItem.chatroom.isSecret
                && oldItem.chatroomImageUrl == newItem.chatroomImageUrl
                && oldItem.isLastItem == newItem.isLastItem
                && memberViewData(oldItem.chatroom.memberViewData, newItem.chatroom.memberViewData)
                && memberViewDataList(oldItem.members, newItem.members)
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

    // todo:
//    private fun communityViewDataList(
//        oldItem: List<BaseViewType>,
//        newItem: List<BaseViewType>
//    ): Boolean {
//        for (i in oldItem.indices) {
//            if (!communityViewData(oldItem[i], newItem[i])) {
//                return false
//            }
//        }
//        return true
//    }

    // todo:
//    private fun communityViewData(
//        oldItem: BaseViewType,
//        newItem: BaseViewType
//    ): Boolean {
//        return if (oldItem is ProgressHorizontalViewData && newItem is ProgressHorizontalViewData) {
//            true
//        } else if (oldItem is ProgressHorizontalViewData && newItem is CommunityViewData) {
//            false
//        } else if (oldItem is CommunityViewData && newItem is ProgressHorizontalViewData) {
//            false
//        } else {
//            return (oldItem as CommunityViewData).id() == (newItem as CommunityViewData).id()
//                    && oldItem.name() == newItem.name()
//                    && oldItem.imageURL() == newItem.imageURL()
//                    && oldItem.collabcardsUnseen() == newItem.collabcardsUnseen()
//                    && oldItem.chatRoomCount() == newItem.chatRoomCount()
//                    && oldItem.pendingMembersCount() == newItem.pendingMembersCount()
//                    && oldItem.pendingChatRoomCount() == newItem.pendingChatRoomCount()
//                    && oldItem.openReportsCount() == newItem.openReportsCount()
//                    && oldItem.clickState() == newItem.clickState()
//                    && memberViewDataList(oldItem.newChatRoomUsers(), newItem.newChatRoomUsers())
//                    && memberViewDataList(
//                oldItem.chatRoomUsers(),
//                newItem.chatRoomUsers()
//            ) && oldItem.updatedAt() == newItem.updatedAt()
//        }
//    }

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