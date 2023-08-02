package com.likeminds.chatmm.chatroom.detail.model

class ChatroomDetailViewData private constructor(
    val chatroom: ChatroomViewData?,
    val actions: List<ChatroomActionViewData>?,
    val participantCount: Int?,
    val canAccessSecretChatRoom: Boolean?,
) {
    class Builder {
        private var chatroom: ChatroomViewData? = null
        private var actions: List<ChatroomActionViewData>? = emptyList()
        private var participantCount: Int? = null
        private var canAccessSecretChatRoom: Boolean? = null

        fun chatroom(chatroom: ChatroomViewData?) = apply { this.chatroom = chatroom }

        fun actions(actions: List<ChatroomActionViewData>?) = apply { this.actions = actions }

        fun participantCount(participantCount: Int?) =
            apply { this.participantCount = participantCount }

        fun canAccessSecretChatRoom(canAccessSecretChatRoom: Boolean?) =
            apply { this.canAccessSecretChatRoom = canAccessSecretChatRoom }

        fun build() = ChatroomDetailViewData(
            chatroom,
            actions,
            participantCount,
            canAccessSecretChatRoom,
        )
    }

    fun toBuilder(): Builder {
        return Builder().chatroom(chatroom)
            .actions(actions)
            .participantCount(participantCount)
            .canAccessSecretChatRoom(canAccessSecretChatRoom)
    }
}