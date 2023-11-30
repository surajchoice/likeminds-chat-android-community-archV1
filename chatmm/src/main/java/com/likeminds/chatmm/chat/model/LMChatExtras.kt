package com.likeminds.chatmm.chat.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMChatExtras private constructor(
    val apiKey: String,
    val userName: String,
    val userId: String?,
    val isGuest: Boolean?,
    val sdkInitiateSource: SDKInitiateSource
) : Parcelable {
    class Builder {
        private var apiKey: String = ""
        private var userName: String = ""
        private var userId: String? = null
        private var isGuest: Boolean? = null
        private var sdkInitiateSource: SDKInitiateSource = SDKInitiateSource.CHAT_FRAGMENT

        fun apiKey(apiKey: String) = apply { this.apiKey = apiKey }
        fun userName(userName: String) = apply { this.userName = userName }
        fun userId(userId: String?) = apply { this.userId = userId }
        fun isGuest(isGuest: Boolean?) = apply { this.isGuest = isGuest }
        fun sdkInitiateSource(sdkInitiateSource: SDKInitiateSource) =
            apply { this.sdkInitiateSource = sdkInitiateSource }

        fun build() = LMChatExtras(
            apiKey,
            userName,
            userId,
            isGuest,
            sdkInitiateSource
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .apiKey(apiKey)
            .userName(userName)
            .userId(userId)
            .isGuest(isGuest)
            .sdkInitiateSource(sdkInitiateSource)
    }
}