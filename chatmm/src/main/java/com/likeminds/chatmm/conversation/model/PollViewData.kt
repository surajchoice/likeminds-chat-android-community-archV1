package com.likeminds.chatmm.conversation.model

import android.os.Parcelable
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.detail.model.MemberViewData
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CHAT_ROOM_POLL
import kotlinx.parcelize.Parcelize

@Parcelize
class PollViewData private constructor(
    var id: String?,
    var text: String,
    var isSelected: Boolean?,
    var percentage: Int?,
    var noVotes: Int?,
    var member: MemberViewData?,
    var toShowResults: Boolean?,
    var parentChatroom: ChatroomViewData?,
    var parentConversation: ConversationViewData?,
    var parentViewItemPosition: Int?,
    var pollSourceType: Int?,
    var pollInfoData: PollInfoData?,
    var parentId: String?// Can be either ChatroomId or ConversationId based on pollSourceType
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_CHAT_ROOM_POLL

    class Builder {
        private var id: String? = null
        private var text: String = ""
        private var isSelected: Boolean? = null
        private var percentage: Int? = null
        private var noVotes: Int? = null
        private var member: MemberViewData? = null
        private var toShowResults: Boolean? = null
        private var parentChatroom: ChatroomViewData? = null
        private var parentConversation: ConversationViewData? = null
        private var parentViewItemPosition: Int? = null
        private var pollSourceType: Int? = null
        private var pollInfoData: PollInfoData? = null
        private var parentId: String? = null


        fun id(id: String?) = apply { this.id = id }
        fun text(text: String) = apply { this.text = text }
        fun isSelected(isSelected: Boolean?) = apply { this.isSelected = isSelected }
        fun percentage(percentage: Int?) = apply { this.percentage = percentage }
        fun noVotes(noVotes: Int?) = apply { this.noVotes = noVotes }
        fun member(member: MemberViewData?) = apply { this.member = member }
        fun toShowResults(toShowResults: Boolean?) = apply { this.toShowResults = toShowResults }
        fun parentChatroom(parentChatroom: ChatroomViewData?) =
            apply { this.parentChatroom = parentChatroom }

        fun parentConversation(parentConversation: ConversationViewData?) =
            apply { this.parentConversation = parentConversation }

        fun parentViewItemPosition(parentViewItemPosition: Int?) =
            apply { this.parentViewItemPosition = parentViewItemPosition }

        fun pollSourceType(pollSourceType: Int?) = apply { this.pollSourceType = pollSourceType }
        fun pollInfoData(pollInfoData: PollInfoData?) = apply { this.pollInfoData = pollInfoData }
        fun parentId(parentId: String?) = apply { this.parentId = parentId }

        fun build() = PollViewData(
            id,
            text,
            isSelected,
            percentage,
            noVotes,
            member,
            toShowResults,
            parentChatroom,
            parentConversation,
            parentViewItemPosition,
            pollSourceType,
            pollInfoData,
            parentId
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .text(text)
            .isSelected(isSelected)
            .percentage(percentage)
            .noVotes(noVotes)
            .member(member)
            .toShowResults(toShowResults)
            .parentChatroom(parentChatroom)
            .parentConversation(parentConversation)
            .parentViewItemPosition(parentViewItemPosition)
            .pollSourceType(pollSourceType)
            .pollInfoData(pollInfoData)
            .parentId(parentId)
    }
}