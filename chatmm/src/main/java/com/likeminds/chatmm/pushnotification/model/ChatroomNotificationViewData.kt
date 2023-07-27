package com.likeminds.chatmm.pushnotification.model

import com.likeminds.chatmm.conversation.model.AttachmentViewData

class ChatroomNotificationViewData private constructor(
    val communityName: String,
    val chatroomName: String,
    val chatroomTitle: String,
    val chatroomUserName: String,
    val chatroomUserImage: String,
    val chatroomId: String,
    val communityImage: String,
    val communityId: Int,
    val route: String,
    val chatroomUnreadConversationCount: Int,
    val chatroomLastConversation: String?,
    val chatroomLastConversationUserName: String?,
    val chatroomLastConversationUserImage: String?,
    val routeChild: String,
    val chatroomLastConversationUserTimestamp: Long?,
    val attachments: List<AttachmentViewData>?,
    val sortKey: String?
) {
    class Builder {
        private var communityName: String = ""
        private var chatroomName: String = ""
        private var chatroomTitle: String = ""
        private var chatroomUserName: String = ""
        private var chatroomUserImage: String = ""
        private var chatroomId: String = ""
        private var communityImage: String = ""
        private var communityId: Int = 0
        private var route: String = ""
        private var chatroomUnreadConversationCount: Int = 0
        private var chatroomLastConversation: String? = null
        private var chatroomLastConversationUserName: String? = null
        private var chatroomLastConversationUserImage: String? = null
        private var routeChild: String = ""
        private var chatroomLastConversationUserTimestamp: Long? = null
        private var attachments: List<AttachmentViewData>? = null
        private var sortKey: String? = null

        fun communityName(communityName: String) = apply { this.communityName = communityName }
        fun chatroomName(chatroomName: String) = apply { this.chatroomName = chatroomName }
        fun chatroomTitle(chatroomTitle: String) = apply { this.chatroomTitle = chatroomTitle }
        fun chatroomUserName(chatroomUserName: String) =
            apply { this.chatroomUserName = chatroomUserName }

        fun chatroomUserImage(chatroomUserImage: String) =
            apply { this.chatroomUserImage = chatroomUserImage }

        fun chatroomId(chatroomId: String) = apply { this.chatroomId = chatroomId }
        fun communityImage(communityImage: String) = apply { this.communityImage = communityImage }
        fun communityId(communityId: Int) = apply { this.communityId = communityId }
        fun route(route: String) = apply { this.route = route }
        fun chatroomUnreadConversationCount(chatroomUnreadConversationCount: Int) =
            apply { this.chatroomUnreadConversationCount = chatroomUnreadConversationCount }

        fun chatroomLastConversation(chatroomLastConversation: String?) =
            apply { this.chatroomLastConversation = chatroomLastConversation }

        fun chatroomLastConversationUserName(chatroomLastConversationUserName: String?) =
            apply { this.chatroomLastConversationUserName = chatroomLastConversationUserName }

        fun chatroomLastConversationUserImage(chatroomLastConversationUserImage: String?) =
            apply { this.chatroomLastConversationUserImage = chatroomLastConversationUserImage }

        fun routeChild(routeChild: String) = apply { this.routeChild = routeChild }
        fun chatroomLastConversationUserTimestamp(chatroomLastConversationUserTimestamp: Long?) =
            apply {
                this.chatroomLastConversationUserTimestamp = chatroomLastConversationUserTimestamp
            }

        fun attachments(attachments: List<AttachmentViewData>?) =
            apply { this.attachments = attachments }

        fun sortKey(sortKey: String?) = apply { this.sortKey = sortKey }

        fun build() = ChatroomNotificationViewData(
            communityName,
            chatroomName,
            chatroomTitle,
            chatroomUserName,
            chatroomUserImage,
            chatroomId,
            communityImage,
            communityId,
            route,
            chatroomUnreadConversationCount,
            chatroomLastConversation,
            chatroomLastConversationUserName,
            chatroomLastConversationUserImage,
            routeChild,
            chatroomLastConversationUserTimestamp,
            attachments,
            sortKey
        )
    }

    fun toBuilder(): Builder {
        return Builder().communityName(communityName)
            .chatroomName(chatroomName)
            .chatroomTitle(chatroomTitle)
            .chatroomUserName(chatroomUserName)
            .chatroomUserImage(chatroomUserImage)
            .chatroomId(chatroomId)
            .communityImage(communityImage)
            .communityId(communityId)
            .route(route)
            .chatroomUnreadConversationCount(chatroomUnreadConversationCount)
            .chatroomLastConversation(chatroomLastConversation)
            .chatroomLastConversationUserName(chatroomLastConversationUserName)
            .chatroomLastConversationUserImage(chatroomLastConversationUserImage)
            .routeChild(routeChild)
            .chatroomLastConversationUserTimestamp(chatroomLastConversationUserTimestamp)
            .attachments(attachments)
            .sortKey(sortKey)
    }
}