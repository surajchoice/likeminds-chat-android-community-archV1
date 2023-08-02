package com.likeminds.chatmm.chatroom.detail.model

import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CONVERSATION_AUTO_FOLLOWED_TAGGED_CHAT_ROOM

class AutoFollowedTaggedActionViewData : BaseViewType {
    override val viewType: Int
        get() = ITEM_CONVERSATION_AUTO_FOLLOWED_TAGGED_CHAT_ROOM
}