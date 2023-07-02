package com.likeminds.chatmm.chatroom.detail.view.adapter

import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.detail.model.MemberViewData
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.conversation.model.ReportLinkExtras
import com.likeminds.chatmm.utils.ValueUtils.getItemInList
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
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
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(0)
        return viewDataBinders
    }

    operator fun get(position: Int): BaseViewType? {
        return items().getItemInList(position)
    }
}

interface ChatroomDetailAdapterListener {
    fun follow(value: Boolean, source: String)
    fun getChatRoomType(): Int?
    fun getChatRoom(): ChatroomViewData?
    fun updateSeenFullConversation(position: Int, alreadySeenFullConversation: Boolean)
    fun isSelectionEnabled(): Boolean
    fun isChatRoomSelected(chatRoomId: String): Boolean
    fun isConversationSelected(conversationId: String): Boolean
    fun scrollToRepliedAnswer(conversation: ConversationViewData, repliedConversationId: String)
    fun scrollToRepliedChatRoom(repliedChatRoomId: String)
    fun isScrolledConversation(position: Int): Boolean
    fun isReportedConversation(conversationId: String?): Boolean
    fun showActionDialogForReportedMessage()
    fun keepFollowingChatRoomClicked()
    fun unFollowChatRoomClicked()
    fun onAudioConversationActionClicked(
        data: AttachmentViewData,
        parentPositionId: String,
        childPosition: Int,
        progress: Int,
    )

    fun onLongPressConversation(
        conversation: ConversationViewData,
        itemPosition: Int,
        from: String,
    )

    fun onConversationSeekbarChanged(
        progress: Int,
        attachmentViewData: AttachmentViewData,
        parentConversationId: String,
        childPosition: Int,
    )

    fun onLongPressChatRoom(chatRoom: ChatroomViewData, itemPosition: Int)
    fun externalLinkClicked(
        conversationId: String?,
        url: String,
        reportLinkExtras: ReportLinkExtras?
    )

    fun onMultipleItemsExpanded(conversation: ConversationViewData, position: Int) {}

    fun observeMediaUpload(uuid: UUID, conversation: ConversationViewData)
    fun onRetryConversationMediaUpload(conversationId: String, attachmentCount: Int)
    fun onFailedConversationClick(conversation: ConversationViewData, itemPosition: Int)
    fun showMemberProfile(member: MemberViewData)

    /**
     * add this function for every navigation from chatroom
     * */
    fun onScreenChanged()
    fun onLinkClicked(conversationId: String?, url: String) {}
}