package com.likeminds.chatmm.homefeed.model

import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CHANNEL_INVITE

class ChannelInviteViewData private constructor(
    val id: Int,
    val invitedChatroom: ChatroomViewData,
    val inviteSender: MemberViewData,
    val inviteReceiver: MemberViewData,
    val inviteStatus: Int,
    val createdAt: Long,
    val updatedAt: Long
) : BaseViewType {

    override val viewType: Int
        get() = ITEM_CHANNEL_INVITE

    class Builder {
        private var id: Int = 0
        private var invitedChatroom: ChatroomViewData = ChatroomViewData.Builder().build()
        private var inviteSender: MemberViewData = MemberViewData.Builder().build()
        private var inviteReceiver: MemberViewData = MemberViewData.Builder().build()
        private var inviteStatus: Int = 0
        private var createdAt: Long = 0
        private var updatedAt: Long = 0

        fun id(id: Int) = apply {
            this.id = id
        }

        fun invitedChatroom(invitedChatroom: ChatroomViewData) = apply {
            this.invitedChatroom = invitedChatroom
        }

        fun inviteSender(inviteSender: MemberViewData) = apply {
            this.inviteSender = inviteSender
        }

        fun inviteReceiver(inviteReceiver: MemberViewData) = apply {
            this.inviteReceiver = inviteReceiver
        }

        fun inviteStatus(inviteStatus: Int) = apply {
            this.inviteStatus = inviteStatus
        }

        fun createdAt(createdAt: Long) = apply {
            this.createdAt = createdAt
        }

        fun updatedAt(updatedAt: Long) = apply {
            this.updatedAt = updatedAt
        }

        fun build() = ChannelInviteViewData(
            id,
            invitedChatroom,
            inviteSender,
            inviteReceiver,
            inviteStatus,
            createdAt,
            updatedAt
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .id(id)
            .invitedChatroom(invitedChatroom)
            .inviteSender(inviteSender)
            .inviteReceiver(inviteReceiver)
            .inviteStatus(inviteStatus)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
    }
}