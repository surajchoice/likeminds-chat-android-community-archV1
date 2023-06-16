package com.likeminds.chatmm.homefeed.model

import android.os.Parcelable
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.detail.model.MemberViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_HOME_CHAT_ROOM
import kotlinx.parcelize.Parcelize

@Parcelize
class ChatViewData private constructor(
    val chatroom: ChatroomViewData,
    val lastConversation: ConversationViewData?,
    val unseenConversationCount: Int,
    val lastConversationTime: String,
    val isDraft: Boolean,
    val chatTypeDrawableId: Int?,
    val members: List<MemberViewData>,
    val lastConversationText: String?,
    val lastConversationMemberName: String?,
    val isLastItem: Boolean,
    val chatroomImageUrl: String?,
) : BaseViewType, Parcelable {
    override val viewType: Int
        get() = ITEM_HOME_CHAT_ROOM

    class Builder {
        private var chatroom: ChatroomViewData = ChatroomViewData.Builder().build()
        private var lastConversation: ConversationViewData? = null
        private var unseenConversationCount: Int = 0
        private var lastConversationTime: String = ""
        private var isDraft: Boolean = false
        private var chatTypeDrawableId: Int? = null
        private var members: List<MemberViewData> = listOf()
        private var lastConversationText: String? = null
        private var lastConversationMemberName: String? = null
        private var isLastItem: Boolean = false
        private var chatroomImageUrl: String? = null

        fun chatroom(chatroom: ChatroomViewData) = apply { this.chatroom = chatroom }
        fun lastConversation(lastConversation: ConversationViewData?) =
            apply { this.lastConversation = lastConversation }

        fun unseenConversationCount(unseenConversationCount: Int) =
            apply { this.unseenConversationCount = unseenConversationCount }

        fun lastConversationTime(lastConversationTime: String) =
            apply { this.lastConversationTime = lastConversationTime }

        fun isDraft(isDraft: Boolean) = apply { this.isDraft = isDraft }
        fun chatTypeDrawableId(chatTypeDrawableId: Int?) =
            apply { this.chatTypeDrawableId = chatTypeDrawableId }

        fun members(members: List<MemberViewData>) = apply { this.members = members }
        fun lastConversationText(lastConversationText: String?) =
            apply { this.lastConversationText = lastConversationText }

        fun lastConversationMemberName(lastConversationMemberName: String?) =
            apply { this.lastConversationMemberName = lastConversationMemberName }

        fun isLastItem(isLastItem: Boolean) = apply { this.isLastItem = isLastItem }
        fun chatroomImageUrl(chatroomImageUrl: String?) =
            apply { this.chatroomImageUrl = chatroomImageUrl }

        fun build() = ChatViewData(
            chatroom,
            lastConversation,
            unseenConversationCount,
            lastConversationTime,
            isDraft,
            chatTypeDrawableId,
            members,
            lastConversationText,
            lastConversationMemberName,
            isLastItem,
            chatroomImageUrl
        )
    }

    fun toBuilder(): Builder {
        return Builder().chatroom(chatroom)
            .lastConversation(lastConversation)
            .unseenConversationCount(unseenConversationCount)
            .lastConversationTime(lastConversationTime)
            .isDraft(isDraft)
            .chatTypeDrawableId(chatTypeDrawableId)
            .members(members)
            .lastConversationText(lastConversationText)
            .lastConversationMemberName(lastConversationMemberName)
            .isLastItem(isLastItem)
            .chatroomImageUrl(chatroomImageUrl)
    }
}