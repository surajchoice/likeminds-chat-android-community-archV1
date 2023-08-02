package com.likeminds.chatmm.polls.model

import android.os.Parcelable
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_POLL
import kotlinx.parcelize.Parcelize

@Parcelize
class PollViewData private constructor(
    val id: String?,
    val text: String,
    val isSelected: Boolean?,
    val percentage: Int?,
    val noVotes: Int?,
    val member: MemberViewData?,
    val toShowResults: Boolean?,
    val parentConversation: ConversationViewData?,
    val parentViewItemPosition: Int?,
    val pollInfoData: PollInfoData?,
    val subText: String?,
    val parentId: String?// Can be either ChatroomId or ConversationId based on pollSourceType
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_POLL

    class Builder {
        private var id: String? = null
        private var text: String = ""
        private var isSelected: Boolean? = null
        private var percentage: Int? = null
        private var noVotes: Int? = null
        private var member: MemberViewData? = null
        private var toShowResults: Boolean? = null
        private var parentConversation: ConversationViewData? = null
        private var parentViewItemPosition: Int? = null
        private var pollInfoData: PollInfoData? = null
        private var subText: String? = null
        private var parentId: String? = null


        fun id(id: String?) = apply { this.id = id }
        fun text(text: String) = apply { this.text = text }
        fun isSelected(isSelected: Boolean?) = apply { this.isSelected = isSelected }
        fun percentage(percentage: Int?) = apply { this.percentage = percentage }
        fun noVotes(noVotes: Int?) = apply { this.noVotes = noVotes }
        fun member(member: MemberViewData?) = apply { this.member = member }
        fun toShowResults(toShowResults: Boolean?) = apply { this.toShowResults = toShowResults }

        fun parentConversation(parentConversation: ConversationViewData?) =
            apply { this.parentConversation = parentConversation }

        fun parentViewItemPosition(parentViewItemPosition: Int?) =
            apply { this.parentViewItemPosition = parentViewItemPosition }

        fun pollInfoData(pollInfoData: PollInfoData?) = apply { this.pollInfoData = pollInfoData }
        fun subText(subText: String?) = apply { this.subText = subText }
        fun parentId(parentId: String?) = apply { this.parentId = parentId }

        fun build() = PollViewData(
            id,
            text,
            isSelected,
            percentage,
            noVotes,
            member,
            toShowResults,
            parentConversation,
            parentViewItemPosition,
            pollInfoData,
            subText,
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
            .parentConversation(parentConversation)
            .parentViewItemPosition(parentViewItemPosition)
            .pollInfoData(pollInfoData)
            .subText(subText)
            .parentId(parentId)
    }
}