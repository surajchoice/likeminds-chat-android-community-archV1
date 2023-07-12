package com.likeminds.chatmm.pushnotification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal class NotificationActionData private constructor(
    var chatroomId: Int?,
    var communityId: Int?,
    var groupRoute: String,
    var childRoute: String,
    var notificationTitle: String,
    var notificationMessage: String,
    var category: String?,
    var subcategory: String?
) : Parcelable {

    class Builder {
        private var chatroomId: Int? = null
        private var communityId: Int? = null
        private var groupRoute: String = ""
        private var childRoute: String = ""
        private var notificationTitle: String = ""
        private var notificationMessage: String = ""
        private var category: String? = null
        private var subcategory: String? = null

        fun chatroomId(chatroomId: Int?) = apply { this.chatroomId = chatroomId }
        fun communityId(communityId: Int?) = apply { this.communityId = communityId }
        fun groupRoute(groupRoute: String) = apply { this.groupRoute = groupRoute }
        fun childRoute(childRoute: String) = apply { this.childRoute = childRoute }
        fun notificationTitle(notificationTitle: String) =
            apply { this.notificationTitle = notificationTitle }

        fun notificationMessage(notificationMessage: String) =
            apply { this.notificationMessage = notificationMessage }

        fun category(category: String?) = apply { this.category = category }
        fun subcategory(subcategory: String?) = apply { this.subcategory = subcategory }

        fun build() = NotificationActionData(
            chatroomId,
            communityId,
            groupRoute,
            childRoute,
            notificationTitle,
            notificationMessage,
            category,
            subcategory
        )
    }

    fun toBuilder(): Builder {
        return Builder().chatroomId(chatroomId)
            .communityId(communityId)
            .groupRoute(groupRoute)
            .childRoute(childRoute)
            .notificationMessage(notificationMessage)
            .notificationTitle(notificationTitle)
            .category(category)
            .subcategory(subcategory)
    }
}