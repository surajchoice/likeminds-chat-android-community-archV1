package com.likeminds.chatsampleapp.auth.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginExtras(
    var isGuest: Boolean = false,
    var userName: String,
    var userId: String? = null,
) : Parcelable