package com.likeminds.chatmm.utils.mediauploader.utils

import androidx.work.Data

object WorkerUtil {

    const val MAX_RETRY_COUNT = 3

    fun Data.getIntOrNull(key: String): Int? {
        val id = getInt(key, -1)
        return if (id == -1) {
            null
        } else {
            id
        }
    }

    fun Data.getBooleanOrNull(key: String): Boolean? {
        val value = getBoolean(key, false)
        return if (!value) {
            null
        } else {
            true
        }
    }
}