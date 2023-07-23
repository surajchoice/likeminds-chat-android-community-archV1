package com.likeminds.chatmm.chatroom.detail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ChatroomDetailResultExtras private constructor(
    val isChatroomDeleted: Boolean?,
    val isChatroomMutedChanged: Boolean?,
    val updatedMuteStatus: Boolean?,
    val isChatroomFollowChanged: Boolean?,
    val updatedFollowStatus: Boolean?,
    val isChatroomNameChanged: Boolean?,
    val updatedChatroomName: String?,
    val chatroomId: String?,
    val reportedConversationId: String?,
    val takeActionOnReportedMessage: Boolean?,
) : Parcelable {
    class Builder {
        private var isChatroomDeleted: Boolean? = null
        private var isChatroomMutedChanged: Boolean? = null
        private var updatedMuteStatus: Boolean? = null
        private var isChatroomFollowChanged: Boolean? = null
        private var updatedFollowStatus: Boolean? = null
        private var isChatroomNameChanged: Boolean? = null
        private var updatedChatroomName: String? = null
        private var chatroomId: String? = null
        private var reportedConversationId: String? = null
        private var takeActionOnReportedMessage: Boolean? = null

        fun isChatroomDeleted(isChatroomDeleted: Boolean?) =
            apply { this.isChatroomDeleted = isChatroomDeleted }

        fun isChatroomMutedChanged(isChatroomMutedChanged: Boolean?) =
            apply { this.isChatroomMutedChanged = isChatroomMutedChanged }

        fun updatedMuteStatus(updatedMuteStatus: Boolean?) =
            apply { this.updatedMuteStatus = updatedMuteStatus }

        fun isChatroomFollowChanged(isChatroomFollowChanged: Boolean?) =
            apply { this.isChatroomFollowChanged = isChatroomFollowChanged }

        fun updatedFollowStatus(updatedFollowStatus: Boolean?) =
            apply { this.updatedFollowStatus = updatedFollowStatus }

        fun isChatroomNameChanged(isChatroomNameChanged: Boolean?) =
            apply { this.isChatroomNameChanged = isChatroomNameChanged }

        fun updatedChatroomName(updatedChatroomName: String?) =
            apply { this.updatedChatroomName = updatedChatroomName }

        fun chatroomId(chatroomId: String?) = apply { this.chatroomId = chatroomId }
        fun reportedConversationId(reportedConversationId: String?) =
            apply { this.reportedConversationId = reportedConversationId }

        fun takeActionOnReportedMessage(takeActionOnReportedMessage: Boolean?) =
            apply { this.takeActionOnReportedMessage = takeActionOnReportedMessage }


        fun build() = ChatroomDetailResultExtras(
            isChatroomDeleted,
            isChatroomMutedChanged,
            updatedMuteStatus,
            isChatroomFollowChanged,
            updatedFollowStatus,
            isChatroomNameChanged,
            updatedChatroomName,
            chatroomId,
            reportedConversationId,
            takeActionOnReportedMessage
        )
    }

    fun toBuilder(): Builder {
        return Builder().isChatroomDeleted(isChatroomDeleted)
            .isChatroomMutedChanged(isChatroomMutedChanged)
            .updatedMuteStatus(updatedMuteStatus)
            .isChatroomFollowChanged(isChatroomFollowChanged)
            .updatedFollowStatus(updatedFollowStatus)
            .isChatroomNameChanged(isChatroomNameChanged)
            .updatedChatroomName(updatedChatroomName)
            .chatroomId(chatroomId)
            .reportedConversationId(reportedConversationId)
            .takeActionOnReportedMessage(takeActionOnReportedMessage)
    }
}