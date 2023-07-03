package com.likeminds.chatmm.chatroom.detail.view.adapter

import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.detail.model.MemberViewData
import com.likeminds.chatmm.chatroom.detail.view.ChatroomDateItemViewDataBinder
import com.likeminds.chatmm.chatroom.detail.view.adapter.databinder.ChatroomItemViewDataBinder
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.conversation.model.ChatroomAnnouncementItemViewDataBinder
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.conversation.model.ReportLinkExtras
import com.likeminds.chatmm.conversation.view.adapter.databinder.*
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.ValueUtils.getItemInList
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CONVERSATION_AUTO_FOLLOWED_TAGGED_CHAT_ROOM
import com.likeminds.chatmm.utils.model.ITEM_CONVERSATION_FOLLOW
import java.util.*

class ChatroomDetailAdapter constructor(
    val sdkPreferences: SDKPreferences,
    val listener: ChatroomDetailAdapterListener,
) : BaseRecyclerAdapter<BaseViewType>() {
    init {
        initViewDataBinders()
    }

    fun getGraphicViewTypes(): Array<Int> {
        return arrayOf(
            ITEM_CONVERSATION_FOLLOW,
            ITEM_CONVERSATION_AUTO_FOLLOWED_TAGGED_CHAT_ROOM,
        )
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(18)

        val chatroomItemViewDataBinder =
            ChatroomItemViewDataBinder(
                sdkPreferences,
//                messageReactionsPreferences,
                listener
            )
        viewDataBinders.add(chatroomItemViewDataBinder)

        val chatroomAnnouncementItemViewDataBinder =
            ChatroomAnnouncementItemViewDataBinder(sdkPreferences, listener)
        viewDataBinders.add(chatroomAnnouncementItemViewDataBinder)

        val conversationMultipleDocumentViewDataBinder =
            ConversationMultipleDocumentViewDataBinder(sdkPreferences, listener)
        viewDataBinders.add(conversationMultipleDocumentViewDataBinder)

        val conversationActionItemViewDataBinder =
            ConversationActionItemViewDataBinder(sdkPreferences, listener)
        viewDataBinders.add(conversationActionItemViewDataBinder)

        val conversationItemViewDataBinder =
            ConversationItemViewDataBinder(
                sdkPreferences,
//                messageReactionsPreferences,
                listener
            )
        viewDataBinders.add(conversationItemViewDataBinder)

        val conversationFollowItemViewDataBinder = ConversationFollowItemViewDataBinder(listener)
        viewDataBinders.add(conversationFollowItemViewDataBinder)

        val conversationAutoFollowedTaggedActionViewDataBinder =
            ConversationAutoFollowedTaggedActionViewDataBinder(listener)
        viewDataBinders.add(conversationAutoFollowedTaggedActionViewDataBinder)

        val conversationSingleImageItemViewDataBinder = ConversationSingleImageItemViewDataBinder(
            sdkPreferences,
//            messageReactionsPreferences,
            listener
        )
        viewDataBinders.add(conversationSingleImageItemViewDataBinder)

        val conversationSingleGifItemViewDataBinder = ConversationSingleGifItemViewDataBinder(
            sdkPreferences,
//            messageReactionsPreferences,
            listener
        )
        viewDataBinders.add(conversationSingleGifItemViewDataBinder)

        val conversationMultipleMediaItemViewDataBinder =
            ConversationMultipleMediaItemViewDataBinder(sdkPreferences, listener)
        viewDataBinders.add(conversationMultipleMediaItemViewDataBinder)

        val conversationSinglePdfItemViewDataBinder = ConversationSinglePdfItemViewDataBinder(
            sdkPreferences,
//            messageReactionsPreferences,
            listener
        )
        viewDataBinders.add(conversationSinglePdfItemViewDataBinder)

        val conversationLinkItemViewDataBinder = ConversationLinkItemViewDataBinder(
            sdkPreferences,
//            messageReactionsPreferences,
            listener
        )
        viewDataBinders.add(conversationLinkItemViewDataBinder)

        val conversationSingleVideoItemViewDataBinder = ConversationSingleVideoItemViewDataBinder(
            sdkPreferences,
//            messageReactionsPreferences,
            listener
        )
        viewDataBinders.add(conversationSingleVideoItemViewDataBinder)

        val conversationAudioItemViewBinder =
            ConversationAudioItemViewBinder(sdkPreferences, listener)
        viewDataBinders.add(conversationAudioItemViewBinder)

        val conversationVoiceNoteItemViewDataBinder = ConversationVoiceNoteItemViewDataBinder(
            sdkPreferences,
//            messageReactionsPreferences,
            listener
        )
        viewDataBinders.add(conversationVoiceNoteItemViewDataBinder)

        val conversationListShimmerViewDataBinder = ConversationListShimmerViewDataBinder()
        viewDataBinders.add(conversationListShimmerViewDataBinder)

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