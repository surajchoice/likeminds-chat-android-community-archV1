package com.likeminds.chatmm.pushnotification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.utils.Route
import org.json.JSONObject

class LMChatNotificationHandler {

    private lateinit var mApplication: Application

    //icon of notification
    private var notificationIcon: Int = 0

    //color of notification text
    private var notificationTextColor: Int = 0

    companion object {
        private var notificationHandler: LMChatNotificationHandler? = null

        const val GENERAL_CHANNEL_ID = "notification_general"
        const val NOTIFICATION_TITLE = "title"
        const val NOTIFICATION_SUB_TITLE = "sub_title"
        const val NOTIFICATION_ROUTE = "route"
        const val NOTIFICATION_CATEGORY = "category"
        const val NOTIFICATION_SUBCATEGORY = "subcategory"
        const val NOTIFICATION_UNREAD_CONVERSATION_GROUP_ID = 101

        private const val NOTIFICATION_DATA = "notification_data"

        @JvmStatic
        fun getInstance(): LMChatNotificationHandler {
            if (notificationHandler == null) {
                notificationHandler = LMChatNotificationHandler()
            }
            return notificationHandler!!
        }
    }


    //create the instance of the handler and channel for notification
    fun create(application: Application) {
        mApplication = application

        notificationIcon = R.drawable.ic_notification

        notificationTextColor = LMBranding.getButtonsColor()

        createNotificationChannel()
    }

    //create notification channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createGeneralNotificationChannel()
        }
    }

    private fun createGeneralNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = mApplication.getString(R.string.general_channel_name)
            val descriptionText = mApplication.getString(R.string.general_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(GENERAL_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                mApplication.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //handle and show notification
    fun handleNotification(data: MutableMap<String, String>) {
        val title = data[NOTIFICATION_TITLE] ?: return
        val subTitle = data[NOTIFICATION_SUB_TITLE] ?: return
        val route = data[NOTIFICATION_ROUTE] ?: return
        val category = data[NOTIFICATION_CATEGORY]
        val subcategory = data[NOTIFICATION_SUBCATEGORY]

        //validate data
        if (category.isNullOrEmpty() && subcategory.isNullOrEmpty()) {
            return
        }

        //create payload for analytics event
        val payloadJson = JSONObject().apply {
            put(NOTIFICATION_TITLE, title)
            put(NOTIFICATION_SUB_TITLE, subTitle)
            put(NOTIFICATION_ROUTE, route)
        }

        //TODO:
//        LMAnalytics.track(
//            LMAnalytics.Events.NOTIFICATION_RECEIVED, hashMapOf(
//                Pair("payload", payloadJson.toString()),
//                Pair(NOTIFICATION_CATEGORY, category),
//                Pair(NOTIFICATION_SUBCATEGORY, subcategory)
//            )
//        )
        //show notifications
        sendNormalNotification(
            mApplication,
            title,
            subTitle,
            route,
            category,
            subcategory
        )
    }

    /**
     * create pending intent and show notifications accordingly
     * */
    private fun sendNormalNotification(
        context: Context,
        title: String,
        subTitle: String,
        route: String,
        category: String?,
        subcategory: String?,
    ) {
        // notificationId is a unique int for each notification that you must define
        val notificationId = route.hashCode()
        val resultPendingIntent: PendingIntent? =
            getRoutePendingIntent(
                context,
                notificationId,
                route,
                title,
                subTitle,
                category,
                subcategory
            )
        val notificationBuilder = NotificationCompat.Builder(mApplication, GENERAL_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(subTitle)
            .setSmallIcon(notificationIcon)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(subTitle))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
        if (resultPendingIntent != null) {
            notificationBuilder.setContentIntent(resultPendingIntent)
        }
        with(NotificationManagerCompat.from(mApplication)) {
            notify(notificationId, notificationBuilder.build())
        }
    }

    //create pending intent as per route in notification
    private fun getRoutePendingIntent(
        context: Context,
        notificationId: Int,
        route: String,
        notificationTitle: String,
        notificationMessage: String,
        category: String?,
        subcategory: String?,
    ): PendingIntent? {
        // todo: analytics
        //get intent for route
        val intent = Route.getRouteIntent(
            context,
            route,
            0,
//            LMAnalytics.Source.NOTIFICATION
        )

        if (intent?.getBundleExtra("bundle") != null) {
            intent.getBundleExtra("bundle")!!.putParcelable(
                NOTIFICATION_DATA,
                NotificationActionData.Builder()
                    .groupRoute(route)
                    .childRoute(route)
                    .notificationTitle(notificationTitle)
                    .notificationMessage(notificationMessage)
                    .category(category)
                    .subcategory(subcategory)
                    .build()
            )
        } else {
            intent?.putExtra(
                NOTIFICATION_DATA, NotificationActionData.Builder()
                    .groupRoute(route)
                    .childRoute(route)
                    .notificationTitle(notificationTitle)
                    .notificationMessage(notificationMessage)
                    .category(category)
                    .subcategory(subcategory)
                    .build()
            )
        }

        var resultPendingIntent: PendingIntent? = null
        if (intent != null) {
            resultPendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        return resultPendingIntent
    }
}