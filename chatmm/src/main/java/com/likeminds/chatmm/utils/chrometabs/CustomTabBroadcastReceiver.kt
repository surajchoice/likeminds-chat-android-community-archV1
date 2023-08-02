package com.likeminds.chatmm.utils.chrometabs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.likeminds.chatmm.conversation.model.ReportLinkExtras

internal class CustomTabBroadcastReceiver : BroadcastReceiver() {

    private val gson = Gson()

    companion object {

        const val ACTION_SHARE = "com.likeminds.utils.chrometabs.ACTION_SHARE"
        const val ACTION_COPY = "com.likeminds.utils.chrometabs.ACTION_COPY"
        const val ACTION_REPORT = "com.likeminds.utils.chrometabs.ACTION_REPORT"

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || intent.data == null || context == null) {
            return
        }

        handleAction(context, intent)
    }

    private fun handleAction(
        context: Context,
        intent: Intent,
    ) {
        when (intent.action) {
            ACTION_SHARE -> {
                CustomTabHelper.shareLink(context, intent.data)
            }
            ACTION_COPY -> CustomTabHelper.copyToClipboard(context, intent.data)
            ACTION_REPORT -> {
                val extras = intent.getStringExtra("extras")
                if (!extras.isNullOrEmpty()) {
                    val data = gson.fromJson(extras, ReportLinkExtras::class.java)
                    if (data != null) {
                        //todo
//                        LikeMindsReportActivity.start(
//                            context,
//                            data,
//                            Intent.FLAG_ACTIVITY_NEW_TASK
//                        )
                    }
                }
            }
        }
    }

}