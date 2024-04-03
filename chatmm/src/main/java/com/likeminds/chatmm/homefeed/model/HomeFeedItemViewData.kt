package com.likeminds.chatmm.homefeed.model

import android.os.Parcelable
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_HOME_CHAT_ROOM
import kotlinx.parcelize.Parcelize

@Parcelize
class HomeFeedItemViewData private constructor(
    val chatroom: ChatroomViewData,
    val lastConversation: ConversationViewData?,
    val unseenConversationCount: Int,
    val lastConversationTime: String,
    val chatTypeDrawableId: Int?,
    val lastConversationText: String?,
    val lastConversationMemberName: String?,
    val isLastItem: Boolean,
    val chatroomImageUrl: String?,
    val dynamicViewType: Int? = null
) : BaseViewType, Parcelable {
    override val viewType: Int
        get() = dynamicViewType ?: ITEM_HOME_CHAT_ROOM

    class Builder {
        private var chatroom: ChatroomViewData = ChatroomViewData.Builder().build()
        private var lastConversation: ConversationViewData? = null
        private var unseenConversationCount: Int = 0
        private var lastConversationTime: String = ""
        private var chatTypeDrawableId: Int? = null
        private var lastConversationText: String? = null
        private var lastConversationMemberName: String? = null
        private var isLastItem: Boolean = false
        private var chatroomImageUrl: String? = null
        private var dynamicViewType: Int? = null

        fun chatroom(chatroom: ChatroomViewData) = apply { this.chatroom = chatroom }
        fun lastConversation(lastConversation: ConversationViewData?) =
            apply { this.lastConversation = lastConversation }

        fun unseenConversationCount(unseenConversationCount: Int) =
            apply { this.unseenConversationCount = unseenConversationCount }

        fun lastConversationTime(lastConversationTime: String) =
            apply { this.lastConversationTime = lastConversationTime }

        fun chatTypeDrawableId(chatTypeDrawableId: Int?) =
            apply { this.chatTypeDrawableId = chatTypeDrawableId }

        fun lastConversationText(lastConversationText: String?) =
            apply { this.lastConversationText = lastConversationText }

        fun lastConversationMemberName(lastConversationMemberName: String?) =
            apply { this.lastConversationMemberName = lastConversationMemberName }

        fun isLastItem(isLastItem: Boolean) = apply { this.isLastItem = isLastItem }
        fun chatroomImageUrl(chatroomImageUrl: String?) =
            apply { this.chatroomImageUrl = chatroomImageUrl }

        fun dynamicViewType(dynamicViewType: Int?) =
            apply { this.dynamicViewType = dynamicViewType }

        fun build() = HomeFeedItemViewData(
            chatroom,
            lastConversation,
            unseenConversationCount,
            lastConversationTime,
            chatTypeDrawableId,
            lastConversationText,
            lastConversationMemberName,
            isLastItem,
            chatroomImageUrl,
            dynamicViewType
        )
    }

    fun toBuilder(): Builder {
        return Builder().chatroom(chatroom)
            .lastConversation(lastConversation)
            .unseenConversationCount(unseenConversationCount)
            .lastConversationTime(lastConversationTime)
            .chatTypeDrawableId(chatTypeDrawableId)
            .lastConversationText(lastConversationText)
            .lastConversationMemberName(lastConversationMemberName)
            .isLastItem(isLastItem)
            .chatroomImageUrl(chatroomImageUrl)
            .dynamicViewType(dynamicViewType)
    }
}