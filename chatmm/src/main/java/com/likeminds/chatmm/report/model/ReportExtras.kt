package com.likeminds.chatmm.report.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ReportExtras private constructor(
    val type: Int,
    val uuid: String?,
    val conversationId: String?,
    val chatroomId: String?,
    val communityId: String?,
    val chatroomName: String?,
    val conversationType: String?,
) : Parcelable {
    class Builder {
        private var type: Int = -1
        private var uuid: String? = null
        private var conversationId: String? = null
        private var chatroomId: String? = null
        private var communityId: String? = null
        private var chatroomName: String? = null
        private var conversationType: String? = null

        fun type(type: Int) = apply { this.type = type }
        fun uuid(uuid: String?) = apply { this.uuid = uuid }
        fun conversationId(conversationId: String?) = apply { this.conversationId = conversationId }
        fun chatroomId(chatroomId: String?) = apply { this.chatroomId = chatroomId }
        fun communityId(communityId: String?) = apply { this.communityId = communityId }
        fun chatroomName(chatroomName: String?) = apply { this.chatroomName = chatroomName }
        fun conversationType(conversationType: String?) =
            apply { this.conversationType = conversationType }

        fun build() = ReportExtras(
            type,
            uuid,
            conversationId,
            chatroomId,
            communityId,
            chatroomName,
            conversationType
        )
    }

    fun toBuilder(): Builder {
        return Builder().uuid(uuid)
            .type(type)
            .conversationId(conversationId)
            .chatroomId(chatroomId)
            .communityId(communityId)
            .chatroomName(chatroomName)
            .conversationType(conversationType)
    }
}