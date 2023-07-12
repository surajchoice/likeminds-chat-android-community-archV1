package com.likeminds.chatmm.conversation.model

import android.os.Parcelable
import com.likeminds.chatmm.chatroom.model.MemberViewData
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_MESSAGE_REACTION
import kotlinx.parcelize.Parcelize

@Parcelize
class ReactionViewData private constructor(
    val memberViewData: MemberViewData,
    val reaction: String,
    val chatroomId: String?,
    val conversationId: String?
) : BaseViewType, Parcelable {

    override val viewType: Int
        get() = ITEM_MESSAGE_REACTION

    class Builder {
        private var memberViewData: MemberViewData = MemberViewData.Builder().build()
        private var reaction: String = ""
        private var chatroomId: String? = null
        private var conversationId: String? = null

        fun memberViewData(memberViewData: MemberViewData) =
            apply { this.memberViewData = memberViewData }

        fun reaction(reaction: String) = apply { this.reaction = reaction }
        fun chatroomId(chatroomId: String?) = apply { this.chatroomId = chatroomId }
        fun conversationId(conversationId: String?) = apply { this.conversationId = conversationId }

        fun build() = ReactionViewData(
            memberViewData,
            reaction,
            chatroomId,
            conversationId
        )
    }

    fun toBuilder(): Builder {
        return Builder().reaction(reaction)
            .memberViewData(memberViewData)
            .chatroomId(chatroomId)
            .conversationId(conversationId)
    }
}