package com.likeminds.chatmm.search.model

import android.os.Parcelable
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_SEARCH_TITLE
import kotlinx.parcelize.Parcelize

@Parcelize
class SearchChatroomTitleViewData private constructor(
    val chatroom: ChatroomViewData,
    val isLast: Boolean?,
    val followStatus: Boolean?,
    val keywordMatchedInCommunityName: List<String>?,
    val keywordMatchedInChatroomName: List<String>?,
    val keywordMatchedInMessageText: List<String>?
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = ITEM_SEARCH_TITLE

    class Builder {
        private lateinit var chatroom: ChatroomViewData
        private var isLast: Boolean? = null
        private var followStatus: Boolean? = null
        private var keywordMatchedInCommunityName: List<String>? = emptyList()
        private var keywordMatchedInChatroomName: List<String>? = emptyList()
        private var keywordMatchedInMessageText: List<String>? = emptyList()

        fun chatroom(chatroom: ChatroomViewData) = apply { this.chatroom = chatroom }

        fun isLast(isLast: Boolean?) = apply { this.isLast = isLast }

        fun followStatus(followStatus: Boolean?) = apply { this.followStatus = followStatus }

        fun keywordMatchedInCommunityName(keywordMatchedInCommunityName: List<String>?) =
            apply { this.keywordMatchedInCommunityName = keywordMatchedInCommunityName }

        fun keywordMatchedInChatroomName(keywordMatchedInChatroomName: List<String>?) =
            apply { this.keywordMatchedInChatroomName = keywordMatchedInChatroomName }

        fun keywordMatchedInMessageText(keywordMatchedInMessageText: List<String>?) =
            apply { this.keywordMatchedInMessageText = keywordMatchedInMessageText }

        fun build() = SearchChatroomTitleViewData(
            chatroom,
            isLast,
            followStatus,
            keywordMatchedInCommunityName,
            keywordMatchedInChatroomName,
            keywordMatchedInMessageText
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .chatroom(chatroom)
            .isLast(isLast)
            .followStatus(followStatus)
            .keywordMatchedInCommunityName(keywordMatchedInCommunityName)
            .keywordMatchedInChatroomName(keywordMatchedInChatroomName)
            .keywordMatchedInMessageText(keywordMatchedInMessageText)
    }
}