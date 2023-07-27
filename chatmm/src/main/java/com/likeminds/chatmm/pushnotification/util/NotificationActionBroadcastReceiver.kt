package com.likeminds.chatmm.pushnotification.util

import android.annotation.SuppressLint
import android.content.*
import android.util.Log
import androidx.core.app.*
import com.google.gson.Gson
import com.likeminds.chatmm.*
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.pushnotification.model.NotificationActionData
import com.likeminds.chatmm.pushnotification.model.NotificationExtras
import com.likeminds.chatmm.pushnotification.viewmodel.LMNotificationViewModel
import com.likeminds.chatmm.utils.Route
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.chatroom.model.*
import com.likeminds.likemindschat.conversation.model.PostConversationRequest
import com.likeminds.likemindschat.conversation.model.SavePostedConversationRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@SuppressLint("CheckResult")
class NotificationActionBroadcastReceiver : BroadcastReceiver() {

    private val lmChatClient = LMChatClient.getInstance()

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var lmNotificationViewModel: LMNotificationViewModel

    @Inject
    lateinit var gson: Gson

    //icon of notification
    private var notificationIcon: Int = 0

    //color of notification text
    private var notificationTextColor: Int = 0

    companion object {
        private const val TAG = "NotificationActionBR"

        const val KEY_REPLY = "reply_text"

        //-------------New chatroom-----------
        const val BUNDLE_NEW_FOLLOW_CHAT_ROOM = "new_follow_chatroom"

        const val BUNDLE_MARK_AS_READ_CHAT_ROOM = "mark_as_read_chatroom"

        const val BUNDLE_REPLY_CHAT_ROOM = "reply_chatroom"

        const val BUNDLE_NEW_REPLY_CHAT_ROOM = "new_reply_chatroom"

        const val BUNDLE_NEW_POLL_VOTE_CHAT_ROOM = "new_poll_vote_chatroom"

        const val ACTION_NEW_CHATROOM_FOLLOW =
            "com.likeminds.utils.notification.ACTION_NEW_CHATROOM_FOLLOW"

        const val ACTION_NEW_CHATROOM_VOTE =
            "com.likeminds.utils.notification.ACTION_NEW_CHATROOM_VOTE"

        const val ACTION_NEW_CHATROOM_REPLY =
            "com.likeminds.utils.notification.ACTION_NEW_CHATROOM_REPLY"

        const val ACTION_CHATROOM_REPLY = "com.likeminds.utils.notification.ACTION_CHATROOM_REPLY"

        const val ACTION_CHATROOM_MARK_AS_READ =
            "com.likeminds.utils.notification.ACTION_CHATROOM_MARK_AS_READ"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // set notification text color as Branding color
        notificationTextColor = LMBranding.getButtonsColor()

        when (intent.action) {
            ACTION_CHATROOM_REPLY -> {
                reply(context, intent)
            }

            ACTION_CHATROOM_MARK_AS_READ -> {
                markAsReadChatroom(context, intent)
            }

            ACTION_NEW_CHATROOM_FOLLOW -> {
                follow(context, intent)
            }

            ACTION_NEW_CHATROOM_VOTE -> {
                vote(context, intent)
            }

            ACTION_NEW_CHATROOM_REPLY -> {
                replyNewChatroom(context, intent)
            }
        }
    }

    private fun replyNewChatroom(context: Context, intent: Intent) {
        val extras = intent.getStringExtra(BUNDLE_NEW_REPLY_CHAT_ROOM)
        val data = gson.fromJson(extras, NotificationExtras::class.java)
        val resultsFromIntent = RemoteInput.getResultsFromIntent(intent)
        var replyText = ""
        if (resultsFromIntent?.containsKey(KEY_REPLY) == true) {
            replyText = resultsFromIntent.getCharSequence(KEY_REPLY).toString()
        }
        if (data == null || replyText.isEmpty()) {
            return
        }
        postReply(context, true, data, replyText)

        // todo: analytics
//        LMAnalytics.track(
//            LMAnalytics.Keys.EVENT_NOTIFICATION_CLICKED,
//            JSONObject().apply {
//                put("payload", gson.toJson(data))
//                put("type_clicked", "cta")
//                put("cta_title", "reply")
//                put("category", data.extraCategory)
//                put("subcategory", data.extraSubcategory)
//            })
    }

    private fun reply(context: Context, intent: Intent) {
        val extras = intent.getStringExtra(BUNDLE_REPLY_CHAT_ROOM)
        val data = gson.fromJson(extras, NotificationExtras::class.java)
        val resultsFromIntent = RemoteInput.getResultsFromIntent(intent)
        var replyText = ""
        if (resultsFromIntent?.containsKey(KEY_REPLY) == true) {
            replyText = resultsFromIntent.getCharSequence(KEY_REPLY).toString()
        }
        if (data == null || replyText.isEmpty()) {
            return
        }
        postReply(context, false, data, replyText)

        // todo: analytics
//        LMAnalytics.track(
//            LMAnalytics.Keys.EVENT_NOTIFICATION_CLICKED,
//            JSONObject().apply {
//                put("payload", gson.toJson(data))
//                put("type_clicked", "cta")
//                put("cta_title", "reply")
//                put("category", data.extraCategory)
//                put("subcategory", data.extraSubcategory)
//            })
    }

    private fun markAsReadChatroom(context: Context, intent: Intent) {
        val extras = intent.getStringExtra(BUNDLE_MARK_AS_READ_CHAT_ROOM)
        val data = gson.fromJson(
            extras,
            NotificationExtras::class.java
        )
        if (data != null) {
            CoroutineScope(Dispatchers.IO).launchIO {
                val request = MarkReadChatroomRequest.Builder()
                    .chatroomId(data.chatroomId)
                    .build()
                val response = lmChatClient.markReadChatroom(request)
                if (response.success) {
                    // todo:
//                    chatroomRepository.setLastSeenTrueAndSaveDraftResponse(
//                        data.chatroomId,
//                        null
//                    )
                    onMarkedAsRead(context, data)
                } else {
                    Log.e(
                        SDKApplication.LOG_TAG,
                        "mark read failed: ${response.errorMessage}"
                    )
                    onMarkedAsRead(context, data)
                }
            }
            // todo: Analytics
//            LMAnalytics.track(
//                LMAnalytics.Keys.EVENT_NOTIFICATION_CLICKED,
//                JSONObject().apply {
//                    put("payload", gson.toJson(data))
//                    put("type_clicked", "cta")
//                    put("cta_title", "mark as read")
//                    put("category", data.extraCategory)
//                    put("subcategory", data.extraSubcategory)
//                })
        } else {
            Log.e(TAG, "notification data is empty")
        }
    }

    private fun follow(context: Context, intent: Intent) {
        val data = intent.getParcelableExtra<NotificationActionData>(BUNDLE_NEW_FOLLOW_CHAT_ROOM)
        if (data != null) {
            CoroutineScope(Dispatchers.IO).launchIO {
                val request = FollowChatroomRequest.Builder()
                    .chatroomId(data.chatroomId.toString())
                    .uuid(userPreferences.getUUID())
                    .value(true)
                    .build()

                val response = lmChatClient.followChatroom(request)
                if (response.success) {
                    onFollowed(context, data)
                } else {
                    Log.e(
                        SDKApplication.LOG_TAG,
                        "chatroom/follow failed: ${response.errorMessage}"
                    )
                    onFollowed(context, data)
                }
            }

            // todo: analytics
//            LMAnalytics.track(
//                LMAnalytics.Keys.EVENT_NOTIFICATION_CLICKED,
//                JSONObject().apply {
//                    put("payload", gson.toJson(data))
//                    put("type_clicked", "cta")
//                    put("cta_title", "follow")
//                    put("category", data.category)
//                    put("subcategory", data.subcategory)
//                })
        }
    }

    private fun vote(context: Context, intent: Intent) {
        val data = intent.getParcelableExtra<NotificationActionData>(BUNDLE_NEW_POLL_VOTE_CHAT_ROOM)
        if (data != null && data.groupRoute.isNotEmpty()) {
            val notificationId = data.groupRoute.hashCode()
            NotificationManagerCompat.from(context).apply {
                cancel(notificationId)
            }

            // todo: analytics
//            LMAnalytics.track(
//                LMAnalytics.Keys.EVENT_NOTIFICATION_CLICKED,
//                JSONObject().apply {
//                    put("payload", gson.toJson(data))
//                    put("type_clicked", "cta")
//                    put("cta_title", "vote")
//                    put("category", data.category)
//                    put("subcategory", data.subcategory)
//                })

            //Open poll chatroom
            context.startActivity(
                Route.getRouteIntent(
                    context, data.groupRoute,
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK,
                    source = LMAnalytics.Source.NOTIFICATION
                )
            )
        }
    }

    private fun postReply(
        context: Context,
        isNewChatroom: Boolean,
        data: NotificationExtras,
        replyText: String
    ) {
        CoroutineScope(Dispatchers.IO).launchIO {
            val request = PostConversationRequest.Builder()
                .chatroomId(data.chatroomId)
                .text(replyText)
                .build()

            val response = lmChatClient.postConversation(request)

            if (response.success) {
                val conversation = response.data?.conversation
                if (conversation != null) {
                    val conversationViewData = ViewDataConverter.convertConversation(conversation)
                    lmNotificationViewModel.sendChatroomResponded(
                        conversationViewData,
                        data.chatroomId,
                        data.notificationTitle
                    )

                    val savePostedConversationRequest = SavePostedConversationRequest.Builder()
                        .conversation(conversation)
                        .isFromNotification(true)
                        .build()
                    // Save the conversation in local db and set all previous conversations as read
                    lmChatClient.savePostedConversation(savePostedConversationRequest)

                    val updateLastSeenAndDraftRequest = UpdateLastSeenAndDraftRequest.Builder()
                        .chatroomId(data.chatroomId)
                        .build()
                    lmChatClient.updateLastSeenAndDraft(updateLastSeenAndDraftRequest)
                }
                val lockScreenSingleNotification =
                    NotificationCompat.Builder(
                        context,
                        LMChatNotificationHandler.GENERAL_CHANNEL_ID
                    )
                        .setSmallIcon(notificationIcon)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(data.title)

                val unreadConversationPerson = Person.Builder()
                    .setKey(data.chatroomId)
                    .setName(userPreferences.getMemberName() ?: "User")
                    .build()

                val time = System.currentTimeMillis()

                val unreadConversationMessagingStyle =
                    NotificationCompat.MessagingStyle(unreadConversationPerson)
                        .addMessage(
                            NotificationCompat.MessagingStyle.Message(
                                replyText,
                                time,
                                unreadConversationPerson
                            )
                        )
                        .setConversationTitle(data.title)
                        .setGroupConversation(true)

                val notificationBuilder =
                    NotificationCompat.Builder(
                        context,
                        LMChatNotificationHandler.GENERAL_CHANNEL_ID
                    )
                        .setSmallIcon(notificationIcon)
                        .setStyle(unreadConversationMessagingStyle)
                        .setAutoCancel(true)
                        .setColor(notificationTextColor)
                        .setWhen(time)
                        .addAction(
                            LMChatNotificationHandler.getReplyAction(
                                context,
                                isNewChatroom,
                                gson,
                                data
                            )
                        )
                        .setShowWhen(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setPublicVersion(lockScreenSingleNotification.build())
                if (!data.childRoute.isNullOrEmpty()) {
                    notificationBuilder.setGroup(data.route)
                    notificationBuilder.setContentIntent(
                        LMChatNotificationHandler.getRoutePendingIntent(
                            context,
                            data.chatroomId.toInt(),
                            data.childRoute,
                            data.notificationTitle,
                            data.notificationMessage,
                            data.extraCategory,
                            data.extraSubcategory
                        )
                    )
                } else {
                    notificationBuilder.setContentIntent(
                        LMChatNotificationHandler.getRoutePendingIntent(
                            context,
                            data.chatroomId.toInt(),
                            data.route!!,
                            data.notificationTitle,
                            data.notificationMessage,
                            data.extraCategory,
                            data.extraSubcategory
                        )
                    )
                }
                if (!isNewChatroom) {
                    notificationBuilder.addAction(
                        LMChatNotificationHandler.getMarkAsReadAction(
                            context,
                            gson,
                            data
                        )
                    )
                }
                NotificationManagerCompat.from(context).apply {
                    notify(data.route, data.chatroomId.toInt(), notificationBuilder.build())
                }
            } else {
                Log.e(SDKApplication.LOG_TAG, "reply failed")
            }
        }
    }

    private fun onFollowed(
        context: Context,
        data: NotificationActionData
    ) {
        if (data.chatroomId != null && data.communityId != null
            && data.groupRoute.isNotEmpty()
        ) {
            NotificationManagerCompat.from(context).apply {
                cancel(data.groupRoute, data.chatroomId!!.toInt())
                cancel(data.groupRoute, data.communityId!!)
            }
        } else if (data.groupRoute.isNotEmpty()) {
            val notificationId = data.groupRoute.hashCode()
            NotificationManagerCompat.from(context).apply {
                cancel(notificationId)
            }
        }

        //Open chatroom
        context.startActivity(
            Route.getRouteIntent(
                context, data.childRoute,
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK,
                source = LMAnalytics.Source.NOTIFICATION
            )
        )
    }

    private fun onMarkedAsRead(
        context: Context,
        data: NotificationExtras
    ) {
        NotificationManagerCompat.from(context).apply {
            cancel(data.route, data.chatroomId.toInt())
        }
        NotificationUtils.removeConversationGroupNotification(context)
    }
}