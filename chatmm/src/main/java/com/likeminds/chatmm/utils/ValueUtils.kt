package com.likeminds.chatmm.utils

import android.util.Patterns

object ValueUtils {
    fun String?.containsUrl(): Boolean {
        this?.let {
            return Patterns.WEB_URL.matcher(this).matches()
        }
        return false
    }

    fun Int.isValidIndex(items: List<*>? = null): Boolean {
        return if (items != null) {
            this > -1 && this < items.size
        } else {
            this > -1
        }
    }
}