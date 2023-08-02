package com.likeminds.chatmm.pushnotification.model

class NotificationExtras private constructor(
    val chatroomId: String,
    val title: String? = null,
    val message: String? = null,
    val route: String? = null,
    val childRoute: String? = null,
    val notificationTitle: String,
    val notificationMessage: String,
    val extraCategory: String? = null,
    val extraSubcategory: String? = null
) {
    class Builder {
        private var chatroomId: String = ""
        private var title: String? = null
        private var message: String? = null
        private var route: String? = null
        private var childRoute: String? = null
        private var notificationTitle: String = ""
        private var notificationMessage: String = ""
        private var extraCategory: String? = null
        private var extraSubcategory: String? = null

        fun chatroomId(chatroomId: String) = apply { this.chatroomId = chatroomId }
        fun title(title: String?) = apply { this.title = title }
        fun message(message: String?) = apply { this.message = message }
        fun route(route: String?) = apply { this.route = route }
        fun childRoute(childRoute: String?) = apply { this.childRoute = childRoute }
        fun notificationTitle(notificationTitle: String) =
            apply { this.notificationTitle = notificationTitle }

        fun notificationMessage(notificationMessage: String) =
            apply { this.notificationMessage = notificationMessage }

        fun extraCategory(extraCategory: String?) = apply { this.extraCategory = extraCategory }
        fun extraSubcategory(extraSubcategory: String?) =
            apply { this.extraSubcategory = extraSubcategory }

        fun build() = NotificationExtras(
            chatroomId,
            title,
            message,
            route,
            childRoute,
            notificationTitle,
            notificationMessage,
            extraCategory,
            extraSubcategory
        )
    }

    fun toBuilder(): Builder {
        return Builder().chatroomId(chatroomId)
            .title(title)
            .message(message)
            .route(route)
            .childRoute(childRoute)
            .notificationTitle(notificationTitle)
            .notificationMessage(notificationMessage)
            .extraCategory(extraCategory)
            .extraSubcategory(extraSubcategory)
    }
}