package com.likeminds.chatmm.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
object TimeUtil {

    private const val APPROX_INITIAL_LAUNCH_MILLIS = 1546281000000
    private const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000

    private val DATE_SDF = object : ThreadLocal<SimpleDateFormat>() {
        override fun initialValue(): SimpleDateFormat {
            return SimpleDateFormat("dd/MM/yy")
        }
    }

    private val TIME_SDF = object : ThreadLocal<SimpleDateFormat>() {
        override fun initialValue(): SimpleDateFormat {
            return SimpleDateFormat("HH:mm")
        }
    }

    /**
     * @param time in millis
     * @return text to show
     */
    fun getLastConversationTime(time: Long?): String {
        if (time == null) {
            return ""
        }
        val newTime = if (isInMillis(time)) {
            time
        } else {
            time * 1000
        }
        val midnightTimestamp = getMidnightTimestamp()
        return when {
            newTime > midnightTimestamp -> {
                TIME_SDF.get()?.format(Date(newTime)) ?: ""
            }
            newTime > (midnightTimestamp - MILLIS_IN_DAY) -> {
                "Yesterday"
            }
            else -> {
                DATE_SDF.get()?.format(Date(newTime)) ?: ""
            }
        }
    }

    fun isInMillis(time: Long): Boolean {
        return time > APPROX_INITIAL_LAUNCH_MILLIS
    }

    private fun getMidnightTimestamp(): Long {
        val date = GregorianCalendar()
        date.set(Calendar.HOUR_OF_DAY, 0)
        date.set(Calendar.MINUTE, 0)
        date.set(Calendar.SECOND, 0)
        date.set(Calendar.MILLISECOND, 0)
        return date.time.time
    }
}