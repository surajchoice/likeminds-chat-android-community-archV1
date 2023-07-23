package com.likeminds.chatmm.polls.view

import android.os.Parcelable
import com.likeminds.chatmm.polls.model.PollViewData
import kotlinx.parcelize.Parcelize

@Parcelize
class PollResultTabExtra private constructor(
    var communityId: String?,
    var chatroomId: String?,
    var conversationId: String?,
    var pollViewData: PollViewData?,
) : Parcelable {

    class Builder {
        private var communityId: String? = null
        private var conversationId: String? = null
        private var chatroomId: String? = null
        private var pollViewData: PollViewData? = null

        fun communityId(communityId: String?) = apply { this.communityId = communityId }
        fun conversationId(conversationId: String?) = apply { this.conversationId = conversationId }
        fun chatroomId(chatroomId: String?) = apply { this.chatroomId = chatroomId }
        fun pollViewData(pollViewData: PollViewData?) = apply { this.pollViewData = pollViewData }

        fun build() = PollResultTabExtra(communityId, chatroomId, conversationId, pollViewData)
    }

    fun toBuilder(): Builder {
        return Builder().communityId(communityId)
            .chatroomId(chatroomId)
            .conversationId(conversationId)
            .pollViewData(pollViewData)
    }
}