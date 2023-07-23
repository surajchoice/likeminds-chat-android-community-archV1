package com.likeminds.chatmm.polls.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class AddPollOptionExtras private constructor(
    val chatroomId: String?,
    val conversationId: String?,
    val pollOptionText: String?
) : Parcelable {

    class Builder {
        private var chatroomId: String? = null
        private var conversationId: String? = null
        private var pollOptionText: String? = null

        fun chatroomId(chatroomId: String?) = apply { this.chatroomId = chatroomId }
        fun conversationId(conversationId: String?) = apply { this.conversationId = conversationId }
        fun pollOptionText(pollOptionText: String?) = apply { this.pollOptionText = pollOptionText }

        fun build() = AddPollOptionExtras(chatroomId, conversationId, pollOptionText)
    }

    fun toBuilder(): Builder {
        return Builder().chatroomId(chatroomId)
            .conversationId(conversationId)
            .pollOptionText(pollOptionText)
    }
}