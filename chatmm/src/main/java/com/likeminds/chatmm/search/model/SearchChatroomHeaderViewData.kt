package com.likeminds.chatmm.search.model

import android.os.Parcelable
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_SEARCH_CHATROOM
import kotlinx.parcelize.Parcelize

@Parcelize
class SearchChatroomHeaderViewData private constructor(
    val chatroom: ChatroomViewData,
    val isLast: Boolean?,
    val followStatus: Boolean?,
    val keywordMatchedInCommunityName: List<String>?,
    val isThirdPartyShare: Boolean?
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = ITEM_SEARCH_CHATROOM

    class Builder {
        private lateinit var chatroom: ChatroomViewData
        private var isLast: Boolean? = null
        private var followStatus: Boolean? = null
        private var keywordMatchedInCommunityName: List<String>? = emptyList()
        private var isThirdPartyShare: Boolean? = null

        fun chatroom(chatroom: ChatroomViewData) = apply { this.chatroom = chatroom }

        fun isLast(isLast: Boolean?) = apply { this.isLast = isLast }

        fun followStatus(followStatus: Boolean?) = apply { this.followStatus = followStatus }

        fun keywordMatchedInCommunityName(keywordMatchedInCommunityName: List<String>?) =
            apply { this.keywordMatchedInCommunityName = keywordMatchedInCommunityName }

        fun isThirdPartyShare(isThirdPartyShare: Boolean?) =
            apply { this.isThirdPartyShare = isThirdPartyShare }

        fun build() = SearchChatroomHeaderViewData(
            chatroom,
            isLast,
            followStatus,
            keywordMatchedInCommunityName,
            isThirdPartyShare
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .chatroom(chatroom)
            .isLast(isLast)
            .followStatus(followStatus)
            .keywordMatchedInCommunityName(keywordMatchedInCommunityName)
            .isThirdPartyShare(isThirdPartyShare)
    }
}
