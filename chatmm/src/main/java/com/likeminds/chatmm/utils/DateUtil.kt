package com.likeminds.chatmm.utils

object DateUtil {
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
}