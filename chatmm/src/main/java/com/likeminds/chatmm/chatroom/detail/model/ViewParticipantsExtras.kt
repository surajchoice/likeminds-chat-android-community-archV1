package com.likeminds.chatmm.chatroom.detail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ViewParticipantsExtras private constructor(
    val communityId: String,
    val chatroomId: String,
    val isSecretChatroom: Boolean,
    val chatroomName: String,
) : Parcelable {
    class Builder {
        private var communityId: String = ""
        private var chatroomId: String = ""
        private var isSecretChatroom: Boolean = false
        private var chatroomName: String = ""

        fun communityId(communityId: String) = apply { this.communityId = communityId }
        fun chatroomId(chatroomId: String) = apply { this.chatroomId = chatroomId }
        fun isSecretChatroom(isSecretChatroom: Boolean) =
            apply { this.isSecretChatroom = isSecretChatroom }

        fun chatroomName(chatroomName: String) = apply { this.chatroomName = chatroomName }

        fun build() = ViewParticipantsExtras(
            communityId,
            chatroomId,
            isSecretChatroom,
            chatroomName
        )
    }

    fun toBuilder(): Builder {
        return Builder().chatroomId(chatroomId)
            .communityId(communityId)
            .isSecretChatroom(isSecretChatroom)
            .chatroomName(chatroomName)
    }
}