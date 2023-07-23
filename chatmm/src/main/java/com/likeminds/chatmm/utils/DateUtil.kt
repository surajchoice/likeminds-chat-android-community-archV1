package com.likeminds.chatmm.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    private const val POLL_ENDED = "Poll ended"

    /**
     * @param milliseconds - This should be the UNIX timestamp
     */
    fun createDateFormat(pattern: String, milliseconds: Long): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(Date(milliseconds * 1000))
    }

    fun formatSeconds(timeInSeconds: Int): String {
        val hours = timeInSeconds / 3600
        val secondsLeft = timeInSeconds - hours * 3600
        val minutes = secondsLeft / 60
        val seconds = secondsLeft - minutes * 60
        var formattedTime = ""
        if (hours in 1..9) formattedTime += "0"
        if (hours > 0)
            formattedTime += "$hours:"
        if (minutes < 10) formattedTime += "0"
        formattedTime += "$minutes:"
        if (seconds < 10) formattedTime += "0"
        formattedTime += seconds
        if (formattedTime.startsWith("00")) {
            return formattedTime.substring(1)
        }
        return formattedTime
    }

    fun getDateTitleForGallery(date: Long): String {
        val targetCalendar = Calendar.getInstance()
        targetCalendar.timeInMillis = date * 1000
        val calendar = Calendar.getInstance()
        return if (isCurrentWeek(calendar, targetCalendar)) "Recent"
        else {
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            if (isPastWeek(calendar, targetCalendar)) "Last Week"
            else {
                calendar.set(Calendar.DAY_OF_WEEK, 1)
                if (isCurrentMonth(calendar, targetCalendar)) "Last Month"
                else getMonthName(date)
            }
        }
    }

    private fun isCurrentWeek(calendar: Calendar, targetCalendar: Calendar): Boolean {
        return calendar[Calendar.WEEK_OF_YEAR] == targetCalendar[Calendar.WEEK_OF_YEAR]
                && calendar[Calendar.YEAR] == targetCalendar[Calendar.YEAR]
    }

    private fun isPastWeek(calendar: Calendar, targetCalendar: Calendar): Boolean {
        return calendar[Calendar.WEEK_OF_YEAR] == targetCalendar[Calendar.WEEK_OF_YEAR]
                && calendar[Calendar.YEAR] == targetCalendar[Calendar.YEAR]
    }

    private fun isCurrentMonth(calendar: Calendar, targetCalendar: Calendar): Boolean {
        return calendar[Calendar.YEAR] == targetCalendar[Calendar.YEAR]
                && calendar[Calendar.MONTH] == targetCalendar[Calendar.MONTH]
                && calendar[Calendar.DAY_OF_MONTH] > targetCalendar[Calendar.DAY_OF_MONTH]
    }

    private fun getMonthName(date: Long): String {
        return SimpleDateFormat("MMMM", Locale.getDefault()).format(date * 1000)
    }

    fun getPollRemainingTime(expiryTime: Long?): String? {
        if (expiryTime != null) {
            val currentDateCalendar = Calendar.getInstance()

            val expireDateCalendar = Calendar.getInstance()
            expireDateCalendar.timeInMillis = expiryTime

            val millisecondsDifference =
                expireDateCalendar.time.time - currentDateCalendar.time.time

            val timeLeftString =
                getTimeLeftString(millisecondsDifference, hasPollOrEventEnded(expiryTime))
            return if (timeLeftString == null) {
                POLL_ENDED
            } else {
                "Ends in $timeLeftString"
            }
        }
        return null
    }

    private fun getTimeLeftString(millisecondsDifference: Long, hasCompleted: Boolean): String? {
        val dayLeft = millisecondsDifference / (24 * 60 * 60 * 1000)
        return if (dayLeft > 1) {
            "$dayLeft days"
        } else if (dayLeft == 1.toLong()) {
            "$dayLeft day"
        } else if (!hasCompleted) {
            when (val hourLeft = millisecondsDifference / (60 * 60 * 1000)) {
                in 2..23 -> "$hourLeft hours"
                1.toLong() -> "$hourLeft hour"
                else -> {
                    when (val minuteLeft = millisecondsDifference / (60 * 1000)) {
                        in 2..59 -> "$minuteLeft mins"
                        1.toLong() -> "$minuteLeft min"
                        else -> "few secs"
                    }
                }
            }
        } else {
            null
        }
    }

    private fun hasPollOrEventEnded(dateTime: Long?): Boolean {
        if (dateTime != null) {
            val dateTimeLeft = dateTime - System.currentTimeMillis()
            if (dateTimeLeft <= 0) {
                return true
            }
        }
        return false
    }

    fun getPollExpireTimeString(expiryTime: Long?): String {
        if (expiryTime != null) {
            val calendarDateTime = Calendar.getInstance()
            calendarDateTime.timeInMillis = expiryTime
            val fullDate = SimpleDateFormat(
                "MMM yyyy, HH:mm",
                Locale.getDefault()
            ).format(calendarDateTime.time)
            val date = SimpleDateFormat("dd", Locale.getDefault()).format(calendarDateTime.time)
            return "$date${DateUtil.getDayOfMonthSuffix(date.toInt())} $fullDate"
        }
        return ""
    }

    private fun getDayOfMonthSuffix(day: Int): String {
        if (day in 1..31) {
            return if (day in 11..13) {
                "th"
            } else when (day % 10) {
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }
        } else {
            throw IllegalArgumentException("illegal day of month: $day")
        }
    }
}