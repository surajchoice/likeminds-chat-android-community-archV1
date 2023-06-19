package com.likeminds.chatmm.chatroom.detail.model

class ChatroomDetailData private constructor(
    var chatroom: ChatroomViewData?,
    var currentMember: MemberViewData?,
    var isMemberNotPartOfCommunity: Boolean,
    var actions: List<ChatroomActionViewData>?,
    var placeholderText: String?,
    var participantCount: Int?,
    var canAccessSecretChatRoom: Boolean?,
) {
    class Builder {
        private var chatroom: ChatroomViewData? = null
        private var currentMember: MemberViewData? = null
        private var isMemberNotPartOfCommunity: Boolean = false
        private var actions: List<ChatroomActionViewData>? = emptyList()
        private var placeholderText: String? = null
        private var participantCount: Int? = null
        private var canAccessSecretChatRoom: Boolean? = null

        fun chatroom(chatroom: ChatroomViewData?) = apply { this.chatroom = chatroom }
        fun currentMember(currentMember: MemberViewData?) =
            apply { this.currentMember = currentMember }

        fun isMemberNotPartOfCommunity(isMemberNotPartOfCommunity: Boolean) =
            apply { this.isMemberNotPartOfCommunity = isMemberNotPartOfCommunity }

        fun actions(actions: List<ChatroomActionViewData>?) = apply { this.actions = actions }
        fun placeholderText(placeholderText: String?) =
            apply { this.placeholderText = placeholderText }

        fun participantCount(participantCount: Int?) =
            apply { this.participantCount = participantCount }

        fun canAccessSecretChatRoom(canAccessSecretChatRoom: Boolean?) =
            apply { this.canAccessSecretChatRoom = canAccessSecretChatRoom }

        fun build() = ChatroomDetailData(
            chatroom,
            currentMember,
            isMemberNotPartOfCommunity,
            actions,
            placeholderText,
            participantCount,
            canAccessSecretChatRoom,
        )
    }

    fun toBuilder(): Builder {
        return Builder().chatroom(chatroom)
            .currentMember(currentMember)
            .isMemberNotPartOfCommunity(isMemberNotPartOfCommunity)
            .actions(actions)
            .placeholderText(placeholderText)
            .participantCount(participantCount)
            .canAccessSecretChatRoom(canAccessSecretChatRoom)
    }
}