package com.likeminds.chatmm.chatroom.detail.view.adapter

import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.detail.model.MemberViewData
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.conversation.model.PollViewData
import com.likeminds.chatmm.utils.ValueUtils.getItemInList
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.DataBoundViewHolder
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import java.util.*

class ChatroomDetailAdapter constructor(
    val listener: ChatroomDetailAdapterListener,
) : BaseRecyclerAdapter<BaseViewType>() {
    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(34)

        val chatroomItemViewDataBinder =
            ChatroomItemViewDataBinder(loginPreferences, messageReactionsPreferences, listener)
        viewDataBinders.add(chatroomItemViewDataBinder)

        val chatroomAnnouncementItemViewDataBinder =
            ChatroomAnnouncementItemViewDataBinder(loginPreferences, listener)
        viewDataBinders.add(chatroomAnnouncementItemViewDataBinder)

        val chatroomDateItemViewDataBinder = ChatroomDateItemViewDataBinder()
        viewDataBinders.add(chatroomDateItemViewDataBinder)

        return viewDataBinders
    }

    operator fun get(position: Int): BaseViewType? {
        return items().getItemInList(position)
    }
}

interface ChatroomDetailAdapterListener {
    fun follow(value: Boolean, source: String)
    fun getPollRemainingTime(expiryTime: Long?): String?
    fun getStartDateTime(chatroomViewData: ChatroomViewData?): String?
    fun getEndDateTime(chatroomViewData: ChatroomViewData?): String?
    fun onConversationPollSubmitClicked(
        conversation: ConversationViewData,
        pollViewDataList: List<PollViewData>,
    )

    fun addParticipantsClicked()
    fun shareClicked()
    fun showMemberProfile(member: MemberViewData)
    fun showToastMessage(message: String)
    fun dismissToastMessage()
    fun addConversationPollOptionClicked(conversationId: String)

    fun showChatRoomPollVotersList(
        chatRoomId: String,
        pollId: String?,
        hasPollEnded: Boolean,
        toShowResult: Boolean?,
        positionOfPoll: Int,
    )

    fun showConversationPollVotersList(
        conversationId: String,
        pollId: String?,
        hasPollEnded: Boolean,
        toShowResult: Boolean?,
        positionOfPoll: Int,
    )

    fun keepFollowingChatRoomClicked()
    fun unFollowChatRoomClicked()

    fun isAdmin(): Boolean

    fun onLongPressChatRoom(chatRoom: ChatroomViewData, itemPosition: Int)
    fun onLongPressConversation(
        conversation: ConversationViewData,
        itemPosition: Int,
        from: String,
    )

    fun onFailedConversationClick(conversation: ConversationViewData, itemPosition: Int)

    fun onMultipleItemsExpanded(conversation: ConversationViewData, position: Int) {}

    fun emoticonGridClicked(
        conversation: ConversationViewData,
        reaction: String?,
        position: Int,
    )

    fun chatroomEmoticonGridClicked(
        chatroomViewData: ChatroomViewData,
        reaction: String?,
        position: Int,
    )

    fun messageReactionHintShown()
    fun updateSeenFullConversation(position: Int, alreadySeenFullConversation: Boolean)
    fun isSelectionEnabled(): Boolean
    fun isChatRoomSelected(chatRoomId: String): Boolean
    fun isConversationSelected(conversationId: String): Boolean
    fun scrollToRepliedAnswer(conversation: ConversationViewData, repliedConversationId: String)
    fun scrollToRepliedChatRoom(repliedChatRoomId: String)
    fun isScrolledConversation(position: Int): Boolean
    fun isReportedConversation(conversationId: String?): Boolean
    fun showActionDialogForReportedMessage()
    fun observeMediaUpload(uuid: UUID, conversation: ConversationViewData)
    fun onRetryConversationMediaUpload(conversationId: String, attachmentCount: Int)
    fun getChatRoomType(): Int?
    fun getChatRoom(): ChatroomViewData?
    fun getBinding(conversationId: String?): DataBoundViewHolder<*>?

    fun onAudioConversationActionClicked(
        data: AttachmentViewData,
        parentPositionId: String,
        childPosition: Int,
        progress: Int,
    )

    fun onAudioChatroomActionClicked(
        data: AttachmentViewData,
        childPosition: Int,
        progress: Int,
    )

    /**
     * add this function for every navigation from chatroom
     * */
    fun onScreenChanged()

    fun onConversationSeekbarChanged(
        progress: Int,
        attachmentViewData: AttachmentViewData,
        parentConversationId: String,
        childPosition: Int,
    )

    fun onChatroomSeekbarChanged(
        progress: Int,
        attachmentViewData: AttachmentViewData,
        childPosition: Int,
    )

    fun onConversationMembersVotedCountClick(
        conversation: ConversationViewData,
        hasPollEnded: Boolean,
        isAnonymous: Boolean?,
        isCreator: Boolean,
    ) {
    }

    fun onChatroomMembersVotedCountClick(
        chatroom: ChatroomViewData,
        hasPollEnded: Boolean,
        isAnonymous: Boolean?,
        isCreator: Boolean,
    ) {
    }

    fun onLinkClicked(conversationId: String?, url: String) {}
}