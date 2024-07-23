package com.likeminds.chatmm.homefeed.model

import android.os.Parcelable
import com.likeminds.likemindschat.chatroom.model.ChannelInviteStatus
import kotlinx.parcelize.Parcelize

@Parcelize
class ChatroomInviteDialogExtras private constructor(
    val chatroomInviteDialogTitle: String,
    val chatroomInviteDialogSubtitle: String,
    val chatroomId: String,
    val channelInviteStatus: ChannelInviteStatus
) : Parcelable {
    class Builder {
        private var chatroomInviteDialogTitle: String = ""
        private var chatroomInviteDialogSubtitle: String = ""
        private var chatroomId: String = ""
        private var channelInviteStatus: ChannelInviteStatus = ChannelInviteStatus.INVITED

        fun chatroomInviteDialogTitle(chatroomInviteDialogTitle: String) = apply {
            this.chatroomInviteDialogTitle = chatroomInviteDialogTitle
        }

        fun chatroomInviteDialogSubtitle(chatroomInviteDialogSubtitle: String) = apply {
            this.chatroomInviteDialogSubtitle = chatroomInviteDialogSubtitle
        }

        fun chatroomId(chatroomId: String) = apply {
            this.chatroomId = chatroomId
        }

        fun channelInviteStatus(channelInviteStatus: ChannelInviteStatus) = apply {
            this.channelInviteStatus = channelInviteStatus
        }

        fun build() = ChatroomInviteDialogExtras(
            chatroomInviteDialogTitle,
            chatroomInviteDialogSubtitle,
            chatroomId,
            channelInviteStatus
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .chatroomId(chatroomId)
            .chatroomInviteDialogTitle(chatroomInviteDialogTitle)
            .chatroomInviteDialogSubtitle(chatroomInviteDialogSubtitle)
            .channelInviteStatus(channelInviteStatus)
    }
}