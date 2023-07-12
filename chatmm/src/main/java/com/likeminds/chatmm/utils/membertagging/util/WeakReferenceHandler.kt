package com.likeminds.chatmm.utils.membertagging.util

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.ref.WeakReference

internal class WeakReferenceHandler(
    memberTaggingTextWatcher: MemberTaggingTextWatcher
) : Handler(Looper.getMainLooper()) {

    companion object {
        private const val TRIGGER_SEARCH_TAG = 1
        private const val SEARCH_TRIGGER_DELAY_IN_MS: Long = 200
    }

    private val userTaggingTextWatcherWeakReference = WeakReference(memberTaggingTextWatcher)

    override fun handleMessage(msg: Message) {
        if (msg.what == TRIGGER_SEARCH_TAG) {
            val userTaggingTextWatcher = userTaggingTextWatcherWeakReference.get()
            userTaggingTextWatcher?.hitUserTaggingApi()
        }
    }

    internal fun removeDelay() {
        removeMessages(TRIGGER_SEARCH_TAG)
    }

    internal fun addDelay() {
        sendEmptyMessageDelayed(
            TRIGGER_SEARCH_TAG,
            SEARCH_TRIGGER_DELAY_IN_MS
        )
    }
}