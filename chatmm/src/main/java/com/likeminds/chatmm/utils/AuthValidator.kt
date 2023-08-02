package com.likeminds.chatmm.utils

import android.util.Patterns

object AuthValidator {
    fun isValidEmail(email: String?): Boolean {
        return if (email.isNullOrEmpty()) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }
}