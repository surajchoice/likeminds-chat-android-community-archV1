package com.likeminds.chatmm.conversation.model

import android.os.Parcelable
import com.likeminds.chatmm.chatroom.detail.model.MemberViewData
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
}