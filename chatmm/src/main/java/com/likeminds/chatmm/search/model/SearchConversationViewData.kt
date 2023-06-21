package com.likeminds.chatmm.search.model

import android.os.Parcelable
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_SEARCH_MESSAGE
import kotlinx.parcelize.Parcelize

@Parcelize
class SearchConversationViewData private constructor(
    val chatroom: ChatroomViewData?,
    val chatroomAnswer: ConversationViewData,
    val chatroomName: String,
    val senderName: String,
    val chatroomAnswerId: String,
    val answer: String,
    val time: String,
    val isLast: Boolean?,
    val followStatus: Boolean?,
    val keywordMatchedInCommunityName: List<String>?,
    val keywordMatchedInChatroomName: List<String>?,
    val keywordMatchedInMessageText: List<String>?
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = ITEM_SEARCH_MESSAGE

    class Builder {
        private var chatroom: ChatroomViewData? = null
        private var chatroomAnswer: ConversationViewData = ConversationViewData.Builder().build()
        private var chatroomName: String? = null
        private var senderName: String? = null
        private var chatroomAnswerId: String? = null
        private var answer: String? = null
        private var time: String? = null
        private var isLast: Boolean? = null
        private var followStatus: Boolean? = null
        private var keywordMatchedInCommunityName: List<String>? = emptyList()
        private var keywordMatchedInChatroomName: List<String>? = emptyList()
        private var keywordMatchedInMessageText: List<String>? = emptyList()

        fun chatroom(chatroom: ChatroomViewData?) = apply { this.chatroom = chatroom }
        fun chatroomAnswer(chatroomAnswer: ConversationViewData) =
            apply { this.chatroomAnswer = chatroomAnswer }

        fun chatroomName(chatroomName: String?) = apply { this.chatroomName = chatroomName }
        fun senderName(senderName: String?) = apply { this.senderName = senderName }
        fun chatroomAnswerId(chatroomAnswerId: String?) =
            apply { this.chatroomAnswerId = chatroomAnswerId }

        fun answer(answer: String?) = apply { this.answer = answer }
        fun time(time: String?) = apply { this.time = time }
        fun isLast(isLast: Boolean?) = apply { this.isLast = isLast }
        fun followStatus(followStatus: Boolean?) = apply { this.followStatus = followStatus }
        fun keywordMatchedInCommunityName(keywordMatchedInCommunityName: List<String>?) =
            apply { this.keywordMatchedInCommunityName = keywordMatchedInCommunityName }

        fun keywordMatchedInChatroomName(keywordMatchedInChatroomName: List<String>?) =
            apply { this.keywordMatchedInChatroomName = keywordMatchedInChatroomName }

        fun keywordMatchedInMessageText(keywordMatchedInMessageText: List<String>?) =
            apply { this.keywordMatchedInMessageText = keywordMatchedInMessageText }


        fun build() = SearchConversationViewData(
            chatroom,
            chatroomAnswer,
            chatroomName.toString(),
            senderName.toString(),
            chatroomAnswerId!!,
            answer.toString(),
            time.toString(),
            isLast,
            followStatus,
            keywordMatchedInCommunityName,
            keywordMatchedInChatroomName,
            keywordMatchedInMessageText
        )
    }

    fun toBuilder(): Builder {
        return Builder().chatroom(chatroom)
            .chatroomAnswer(chatroomAnswer)
            .chatroomName(chatroomName)
            .senderName(senderName)
            .chatroomAnswerId(chatroomAnswerId)
            .answer(answer)
            .time(time)
            .isLast(isLast)
            .followStatus(followStatus)
            .keywordMatchedInCommunityName(keywordMatchedInCommunityName)
            .keywordMatchedInChatroomName(keywordMatchedInChatroomName)
            .keywordMatchedInMessageText(keywordMatchedInMessageText)
    }
}