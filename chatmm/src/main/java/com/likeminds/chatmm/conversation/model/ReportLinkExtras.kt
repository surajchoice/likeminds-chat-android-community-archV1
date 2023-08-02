package com.likeminds.chatmm.conversation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ReportLinkExtras private constructor(
    val chatroomId: String,
    val conversationId: String?,
    val reportedMemberId: String?,
    val link: String? = null
) : Parcelable {
    class Builder {
        private var chatroomId: String = ""
        private var conversationId: String? = null
        private var reportedMemberId: String? = null
        private var link: String? = null

        fun chatroomId(chatroomId: String) = apply { this.chatroomId = chatroomId }
        fun conversationId(conversationId: String?) = apply { this.conversationId = conversationId }
        fun reportedMemberId(reportedMemberId: String?) =
            apply { this.reportedMemberId = reportedMemberId }

        fun link(link: String?) = apply { this.link = link }

        fun build() = ReportLinkExtras(
            chatroomId,
            conversationId,
            reportedMemberId,
            link
        )
    }

    fun toBuilder(): Builder {
        return Builder().chatroomId(chatroomId)
            .conversationId(conversationId)
            .reportedMemberId(reportedMemberId)
            .link(link)
    }
}