package com.likeminds.chatmm.polls.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PollResultExtras private constructor(
    val communityId: String?,
    val communityName: String?,
    val conversationId: String?,
    val chatroomId: String?,
    val selectedPoll: Int
) : Parcelable {

    class Builder {
        private var communityId: String? = null
        private var communityName: String? = null
        private var conversationId: String? = null
        private var chatroomId: String? = null
        private var selectedPoll: Int = -1

        fun communityId(communityId: String?) = apply { this.communityId = communityId }
        fun communityName(communityName: String?) = apply { this.communityName = communityName }
        fun conversationId(conversationId: String?) =
            apply { this.conversationId = conversationId }

        fun chatroomId(chatroomId: String?) = apply { this.chatroomId = chatroomId }
        fun selectedPoll(selectedPoll: Int) = apply { this.selectedPoll = selectedPoll }

        fun build() =
            PollResultExtras(communityId, communityName, conversationId, chatroomId, selectedPoll)
    }

    fun toBuilder(): Builder {
        return Builder().communityId(communityId)
            .communityName(communityName)
            .chatroomId(chatroomId)
            .conversationId(conversationId)
            .selectedPoll(selectedPoll)
    }
}