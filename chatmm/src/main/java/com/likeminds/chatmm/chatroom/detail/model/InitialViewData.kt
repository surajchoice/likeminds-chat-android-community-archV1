package com.likeminds.chatmm.chatroom.detail.model

import com.likeminds.chatmm.utils.model.BaseViewType

class InitialViewData private constructor(
    val chatRoom: ChatroomViewData?,
    val data: List<BaseViewType>,
    val scrollPosition: Int
) {
    class Builder {
        private var chatRoom: ChatroomViewData? = null
        private var data: List<BaseViewType> = emptyList()
        private var scrollPosition: Int = SCROLL_DOWN

        fun chatroom(chatRoom: ChatroomViewData?) = apply { this.chatRoom = chatRoom }
        fun data(data: List<BaseViewType>) = apply { this.data = data }
        fun scrollPosition(scrollPosition: Int) = apply { this.scrollPosition = scrollPosition }

        fun build() = InitialViewData(chatRoom, data, scrollPosition)
    }

    fun toBuilder(): Builder {
        return Builder().chatroom(chatRoom)
            .data(data)
            .scrollPosition(scrollPosition)
    }
}