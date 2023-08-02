package com.likeminds.chatmm.utils

object ErrorUtil {
    const val UNKNOWN_ERROR = "Some unknown error occurred"

    fun emptyExtrasException(className: String) =
        IllegalStateException("$className cannot be called without passing a valid bundle")
}