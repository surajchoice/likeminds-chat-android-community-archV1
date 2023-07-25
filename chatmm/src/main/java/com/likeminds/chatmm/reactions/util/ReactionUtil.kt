package com.likeminds.chatmm.reactions.util

import android.util.Log
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.reactions.model.ReactionViewData
import com.likeminds.chatmm.reactions.model.ReactionsGridViewData

object ReactionUtil {

    // groups each reaction of a conversation in a map
    fun getMessageReactions(
        conversationViewData: ConversationViewData
    ): Map<String, List<ReactionViewData>>? {
        val reactions = conversationViewData.reactions
        Log.d(
            "123456789", """
            getMessageReactions
            name: ${reactions?.first()?.memberViewData?.name}
            uuid: ${reactions?.first()?.memberViewData?.sdkClientInfo?.uuid}
        """.trimIndent()
        )
        return reactions?.groupBy {
            it.reaction
        }
    }

    // groups each reaction of a chatroom in a map
    fun getChatroomReactions(
        chatroomViewData: ChatroomViewData,
    ): Map<String, List<ReactionViewData>>? {
        val reactions = chatroomViewData.reactions
        return reactions?.groupBy {
            it.reaction
        }
    }

    fun getChatroomReactionsGrid(
        chatroomViewData: ChatroomViewData
    ): ReactionsGridViewData? {
        val reactions = chatroomViewData.reactions
        if (!reactions.isNullOrEmpty()) {
            val builder = ReactionsGridViewData.Builder()
            val reactionsHashmap = reactions.groupingBy {
                it.reaction
            }.eachCount().entries.take(3)

            val size = reactionsHashmap.size
            if (size >= 2) {
                builder.mostRecentReaction(reactionsHashmap[0].key)
                builder.mostRecentReactionCount(reactionsHashmap[0].value)
                builder.secondMostRecentReaction(reactionsHashmap[1].key)
                builder.secondMostRecentReactionCount(reactionsHashmap[1].value)

                if (size > 2) {
                    builder.moreThanTwoReactionsPresent(true)
                } else {
                    builder.moreThanTwoReactionsPresent(false)
                }
            } else if (reactionsHashmap.size == 1) {
                builder.mostRecentReaction(reactionsHashmap[0].key)
                builder.mostRecentReactionCount(reactionsHashmap[0].value)
                builder.moreThanTwoReactionsPresent(false)
                builder.secondMostRecentReaction(null)
            }
            return builder.build()
        } else {
            return null
        }
    }

    fun getReactionsGrid(
        conversationViewData: ConversationViewData
    ): ReactionsGridViewData? {
        val reactions = conversationViewData.reactions
        if (!reactions.isNullOrEmpty()) {
            val builder = ReactionsGridViewData.Builder()
            val reactionsHashmap = reactions.groupingBy {
                it.reaction
            }.eachCount().entries.take(3)

            val size = reactionsHashmap.size
            if (size >= 2) {
                builder.mostRecentReaction(reactionsHashmap[0].key)
                builder.mostRecentReactionCount(reactionsHashmap[0].value)
                builder.secondMostRecentReaction(reactionsHashmap[1].key)
                builder.secondMostRecentReactionCount(reactionsHashmap[1].value)

                if (size > 2) {
                    builder.moreThanTwoReactionsPresent(true)
                } else {
                    builder.moreThanTwoReactionsPresent(false)
                }
            } else if (reactionsHashmap.size == 1) {
                builder.mostRecentReaction(reactionsHashmap[0].key)
                builder.mostRecentReactionCount(reactionsHashmap[0].value)
                builder.moreThanTwoReactionsPresent(false)
                builder.secondMostRecentReaction(null)
            }
            return builder.build()
        } else {
            return null
        }
    }
}