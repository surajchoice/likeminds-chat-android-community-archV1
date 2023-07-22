package com.likeminds.chatmm.homefeed.util

import android.content.Context
import com.likeminds.chatmm.R
import com.likeminds.chatmm.chatroom.detail.util.ChatroomUtil
import com.likeminds.chatmm.homefeed.model.ContentHeaderViewData
import com.likeminds.chatmm.homefeed.model.EmptyScreenViewData
import com.likeminds.chatmm.homefeed.model.HomeFeedItemViewData
import com.likeminds.chatmm.member.util.MemberUtil
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.utils.TimeUtil
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.likemindschat.chatroom.model.Chatroom

object HomeFeedUtil {
    fun getEmptyChatView(context: Context): EmptyScreenViewData {
        return EmptyScreenViewData.Builder()
            .title(context.getString(R.string.empty_chat_room_title))
            .subTitle("")
            .build()
    }

    fun getContentHeaderView(title: String): ContentHeaderViewData {
        return ContentHeaderViewData.Builder()
            .title(title)
            .build()
    }

    fun getChatRoomViewData(
        chatroom: Chatroom,
        userPreferences: UserPreferences
    ): HomeFeedItemViewData {
        val chatroomViewData = ViewDataConverter.convertChatroom(chatroom)
        val lastConversation =
            ViewDataConverter.convertConversation(chatroom.lastConversation)

        val lastConversationMemberName = MemberUtil.getFirstNameToShow(
            userPreferences,
            lastConversation?.memberViewData
        )
        val lastConversationText = ChatroomUtil.getLastConversationTextForHome(lastConversation)
        val lastConversationTime = if (chatroom.isDraft == true) {
            chatroomViewData.cardCreationTime ?: ""
        } else {
            TimeUtil.getLastConversationTime(chatroomViewData.updatedAt)
        }
        return HomeFeedItemViewData.Builder()
            .chatroom(chatroomViewData)
            .lastConversation(lastConversation)
            .lastConversationTime(lastConversationTime)
            .unseenConversationCount(chatroomViewData.unseenCount ?: 0)
            .chatTypeDrawableId(ChatroomUtil.getTypeDrawableId(chatroomViewData.type))
            .lastConversationText(lastConversationText)
            .lastConversationMemberName(lastConversationMemberName)
            .isLastItem(true)
            .chatroomImageUrl(chatroomViewData.chatroomImageUrl)
            .build()
    }
}