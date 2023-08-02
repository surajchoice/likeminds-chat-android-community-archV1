package com.likeminds.chatmm.reactions.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.reactions.model.ReactionViewData
import com.likeminds.chatmm.reactions.util.ReactionsPreferences
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.conversation.model.DeleteReactionRequest
import com.likeminds.likemindschat.conversation.model.PutReactionRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class ReactionsViewModel @Inject constructor(
    private val reactionsPreferences: ReactionsPreferences
) : ViewModel() {

    private val lmChatClient = LMChatClient.getInstance()

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageFlow = errorEventChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class PutReaction(val errorMessage: String?) : ErrorMessageEvent()
        data class DeleteReaction(val errorMessage: String?) : ErrorMessageEvent()
    }

    fun putConversationReaction(
        conversationId: String,
        messageReactionViewData: ReactionViewData
    ) {
        viewModelScope.launchIO {
            setUserHasReacted()
            val request = PutReactionRequest.Builder()
                .conversationId(conversationId)
                .reaction(messageReactionViewData.reaction)
                .build()

            val response = lmChatClient.putReaction(request)
            if (!response.success) {
                val errorMessage = response.errorMessage
                Log.e(SDKApplication.LOG_TAG, "conversation reaction add failed: $errorMessage")
                errorEventChannel.send(ErrorMessageEvent.PutReaction(errorMessage))
            }
        }
    }

    fun putChatroomReaction(chatroomId: String, reactionViewData: ReactionViewData) {
        viewModelScope.launchIO {
            setUserHasReacted()
            val request = PutReactionRequest.Builder()
                .reaction(reactionViewData.reaction)
                .chatroomId(chatroomId)
                .build()

            val response = lmChatClient.putReaction(request)

            if (!response.success) {
                val errorMessage = response.errorMessage
                Log.e(SDKApplication.LOG_TAG, "chatroom reaction add failed: $errorMessage")
                errorEventChannel.send(ErrorMessageEvent.PutReaction(errorMessage))
            }
        }
    }

    fun deleteConversationReaction(conversationId: String) {
        viewModelScope.launchIO {
            val request = DeleteReactionRequest.Builder()
                .conversationId(conversationId)
                .build()

            val response = lmChatClient.deleteReaction(request)
            if (!response.success) {
                val errorMessage = response.errorMessage
                Log.e(
                    SDKApplication.LOG_TAG,
                    "conversation reaction remove failed: $errorMessage"
                )
                errorEventChannel.send(ErrorMessageEvent.DeleteReaction(errorMessage))
            }
        }
    }

    fun deleteChatroomReaction(chatroomId: String) {
        viewModelScope.launchIO {
            val request = DeleteReactionRequest.Builder()
                .chatroomId(chatroomId)
                .build()

            val response = lmChatClient.deleteReaction(request)
            if (!response.success) {
                val errorMessage = response.errorMessage
                Log.e(SDKApplication.LOG_TAG, "chatroom reaction remove failed: $errorMessage")
                errorEventChannel.send(ErrorMessageEvent.DeleteReaction(errorMessage))
            }
        }
    }

    private fun setUserHasReacted() {
        if (!reactionsPreferences.getHasUserReactedOnce()) {
            reactionsPreferences.setHasUserReactedOnce(true)
        }
    }

    fun reactionHintShown() {
        val timesAlreadyShown = reactionsPreferences.getNoOfTimesHintShown()
        reactionsPreferences.setNoOfTimesHintShown(timesAlreadyShown + 1)
    }

    /**------------------------------------------------------------
     * Analytics events
    ---------------------------------------------------------------*/

    /**
     * Triggers when a puts a reaction on a conversation/chatroom
     **/
    fun sendReactionAddedEvent(
        reaction: String,
        from: String,
        conversationId: String,
        chatroomId: String,
        communityId: String?,
    ) {
        LMAnalytics.track(
            LMAnalytics.Events.REACTION_ADDED,
            mapOf(
                "reaction" to reaction,
                "from" to from,
                LMAnalytics.Keys.MESSAGE_ID to conversationId,
                LMAnalytics.Keys.CHATROOM_ID to chatroomId,
                LMAnalytics.Keys.COMMUNITY_ID to communityId
            )
        )
    }

    /**
     * Triggers when the user long presses the entity and emoticon tray is opened
     **/
    fun sendEmoticonTrayOpenedEvent(
        from: String,
        conversationId: String,
        chatroomId: String,
        communityId: String?,
    ) {
        LMAnalytics.track(
            LMAnalytics.Events.EMOTICON_TRAY_OPENED,
            mapOf(
                "from" to from,
                LMAnalytics.Keys.MESSAGE_ID to conversationId,
                LMAnalytics.Keys.CHATROOM_ID to chatroomId,
                LMAnalytics.Keys.COMMUNITY_ID to communityId
            )
        )
    }

    /**
     * Triggers when the user removes a reaction
     **/
    fun sendReactionRemovedEvent(
        conversationId: String,
        chatroomId: String,
        communityId: String?,
    ) {
        LMAnalytics.track(
            LMAnalytics.Events.REACTION_REMOVED,
            mapOf(
                LMAnalytics.Keys.MESSAGE_ID to conversationId,
                LMAnalytics.Keys.CHATROOM_ID to chatroomId,
                LMAnalytics.Keys.COMMUNITY_ID to communityId
            )
        )
    }

    fun sendReactionListOpenedEvent(
        conversationId: String,
        chatroomId: String,
        communityId: String,
    ) {
        LMAnalytics.track(
            LMAnalytics.Events.REACTION_LIST_OPENED,
            mapOf(
                LMAnalytics.Keys.MESSAGE_ID to conversationId,
                LMAnalytics.Keys.CHATROOM_ID to chatroomId,
                LMAnalytics.Keys.COMMUNITY_ID to communityId
            )
        )
    }
}