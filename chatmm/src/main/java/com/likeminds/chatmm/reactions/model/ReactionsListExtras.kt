package com.likeminds.chatmm.reactions.model

import android.os.Parcelable
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import kotlinx.parcelize.Parcelize

@Parcelize
class ReactionsListExtras private constructor(
    val conversation: ConversationViewData?,
    val chatroom: ChatroomViewData?,
    val gridPositionClicked: Int?,
    val isConversation: Boolean
) : Parcelable {
    class Builder {
        private var conversation: ConversationViewData? = null
        private var chatroom: ChatroomViewData? = null
        private var gridPositionClicked: Int? = null
        private var isConversation: Boolean = false

        fun conversation(conversation: ConversationViewData?) =
            apply { this.conversation = conversation }

        fun chatroom(chatroom: ChatroomViewData?) = apply { this.chatroom = chatroom }
        fun gridPositionClicked(gridPositionClicked: Int?) =
            apply { this.gridPositionClicked = gridPositionClicked }

        fun isConversation(isConversation: Boolean) = apply { this.isConversation = isConversation }

        fun build() =
            ReactionsListExtras(conversation, chatroom, gridPositionClicked, isConversation)
    }

    fun toBuilder(): Builder {
        return Builder().chatroom(chatroom)
            .conversation(conversation)
            .gridPositionClicked(gridPositionClicked)
            .isConversation(isConversation)
    }
}