package com.likeminds.chatmm.homefeed.model

import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CHATROOM_LIST_SHIMMER_VIEW

class ChatroomListShimmerViewData private constructor() : BaseViewType {
    override val viewType: Int
        get() = ITEM_CHATROOM_LIST_SHIMMER_VIEW

    class Builder {
        fun build() = ChatroomListShimmerViewData()
    }
}