package com.likeminds.chatmm.pushnotification.util

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.likeminds.chatmm.pushnotification.util.LMChatNotificationHandler.Companion.NOTIFICATION_UNREAD_CONVERSATION_GROUP_ID

object NotificationUtils {

    private const val NOTIFICATION_TAG = "chatroom_followed_feed"

    fun removeConversationNotification(context: Context, chatroomId: String) {
        val notificationId = chatroomId.toIntOrNull() ?: return
        NotificationManagerCompat.from(context).apply {
            cancel(NOTIFICATION_TAG, notificationId)
        }
        try {
            removeConversationGroupNotification(context)
        } catch (e: Exception) {
            Log.e("Error", "${e.stackTrace}")
        }
    }

    fun removeConversationGroupNotification(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            val activeNotifications = manager.activeNotifications.filter {
                it.tag == NOTIFICATION_TAG
            }
            if (activeNotifications.size == 1) {
                val notification = activeNotifications.find {
                    it.id == NOTIFICATION_UNREAD_CONVERSATION_GROUP_ID
                } ?: return
                NotificationManagerCompat.from(context).apply {
                    cancel(notification.tag, NOTIFICATION_UNREAD_CONVERSATION_GROUP_ID)
                }
            }
        }
    }
}