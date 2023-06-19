package com.likeminds.chatmm.chatroom.detail.model

import androidx.annotation.DrawableRes

class ChatReplyViewData private constructor(
    val memberName: String,
    val conversationText: String?,
    @DrawableRes
    val drawable: Int?,
    val imageUrl: String,
    val attachmentType: String,
    val isEditMessage: Boolean,
    val isMessageDeleted: Boolean,
    val type: String?,
    val repliedMemberState: String?,
    val deleteMessage: String?
) {
    class Builder {
        private var memberName: String = ""
        private var conversationText: String? = null

        @DrawableRes
        private var drawable: Int? = null
        private var imageUrl: String = ""
        private var attachmentType: String = ""
        private var isEditMessage: Boolean = false
        private var isMessageDeleted: Boolean = false
        private var type: String? = null
        private var repliedMemberState: String? = null
        private var deleteMessage: String? = null

        fun memberName(memberName: String) = apply { this.memberName = memberName }
        fun conversationText(conversationText: String?) =
            apply { this.conversationText = conversationText }

        fun drawable(@DrawableRes drawable: Int?) = apply { this.drawable = drawable }
        fun imageUrl(imageUrl: String) = apply { this.imageUrl = imageUrl }
        fun attachmentType(attachmentType: String) = apply { this.attachmentType = attachmentType }
        fun isEditMessage(isEditMessage: Boolean) = apply { this.isEditMessage = isEditMessage }
        fun isMessageDeleted(isMessageDeleted: Boolean) =
            apply { this.isMessageDeleted = isMessageDeleted }

        fun type(type: String?) = apply { this.type = type }
        fun repliedMemberState(repliedMemberState: String?) =
            apply { this.repliedMemberState = repliedMemberState }

        fun deleteMessage(deleteMessage: String?) = apply { this.deleteMessage = deleteMessage }
    }

    fun toBuilder(): Builder {
        return Builder().memberName(memberName)
            .conversationText(conversationText)
            .drawable(drawable)
            .imageUrl(imageUrl)
            .attachmentType(attachmentType)
            .isEditMessage(isEditMessage)
            .isMessageDeleted(isMessageDeleted)
            .type(type)
            .repliedMemberState(repliedMemberState)
            .deleteMessage(deleteMessage)
    }
}