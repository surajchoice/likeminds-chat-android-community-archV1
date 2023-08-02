package com.likeminds.chatmm.utils

object ViewMoreUtil {

    /**
     * This function is for getting short Answers for the read more feature
     */
    fun getShortAnswer(answer: String?, readMoreCountLimit: Int): String? {
        if (answer == null)
            return null
        return if (answer.length > readMoreCountLimit) {
            answer.substring(0, readMoreCountLimit)
        } else {
            null
        }
    }
}