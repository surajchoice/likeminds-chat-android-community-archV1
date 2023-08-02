package com.likeminds.chatmm.homefeed.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class HomeFeedExtras private constructor(
    val apiKey: String,
    val userName: String,
    val userId: String?,
    val isGuest: Boolean?
) : Parcelable {
    class Builder {
        private var apiKey: String = ""
        private var userName: String = ""
        private var userId: String? = null
        private var isGuest: Boolean? = null

        fun apiKey(apiKey: String) = apply { this.apiKey = apiKey }
        fun userName(userName: String) = apply { this.userName = userName }
        fun userId(userId: String?) = apply { this.userId = userId }
        fun isGuest(isGuest: Boolean?) = apply { this.isGuest = isGuest }

        fun build() = HomeFeedExtras(
            apiKey,
            userName,
            userId,
            isGuest
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .apiKey(apiKey)
            .userName(userName)
            .userId(userId)
            .isGuest(isGuest)
    }
}