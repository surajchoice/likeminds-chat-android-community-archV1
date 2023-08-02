package com.likeminds.chatmm.chatroom.detail.model

import com.likeminds.chatmm.utils.model.BaseViewType

class PaginatedViewData private constructor(
    @ScrollState val scrollState: Int, // Scrolling up or down
    val data: List<BaseViewType>, // Contains the paginated data
    @ScrollState val extremeScrollTo: Int?, // Do we need to scroll to extreme top or bottom
    val repliedConversationId: String?, // Replied conversation id if pagination was called due to click on it
    val repliedChatRoomId: String?, // Replied chatRoom id if pagination was called due to click on it
) {
    class Builder {
        private var scrollState: Int = 0
        private var data: List<BaseViewType> = emptyList()
        private var extremeScrollTo: Int? = null
        private var repliedConversationId: String? = null
        private var repliedChatRoomId: String? = null

        fun scrollState(scrollState: Int) = apply { this.scrollState = scrollState }
        fun data(data: List<BaseViewType>) = apply { this.data = data }
        fun extremeScrollTo(extremeScrollTo: Int?) =
            apply { this.extremeScrollTo = extremeScrollTo }

        fun repliedConversationId(repliedConversationId: String?) =
            apply { this.repliedConversationId = repliedConversationId }

        fun repliedChatRoomId(repliedChatRoomId: String?) =
            apply { this.repliedChatRoomId = repliedChatRoomId }

        fun build() = PaginatedViewData(
            scrollState,
            data,
            extremeScrollTo,
            repliedConversationId,
            repliedChatRoomId
        )
    }

    fun toBuilder(): Builder {
        return Builder().scrollState(scrollState)
            .data(data)
            .extremeScrollTo(extremeScrollTo)
            .repliedConversationId(repliedConversationId)
            .repliedChatRoomId(repliedChatRoomId)
    }
}