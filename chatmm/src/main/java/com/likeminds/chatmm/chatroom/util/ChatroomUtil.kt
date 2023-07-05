package com.likeminds.chatmm.chatroom.util

import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.conversation.model.ConversationState
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.conversation.model.STATE_NORMAL
import com.likeminds.chatmm.media.model.InternalMediaType

object ChatroomUtil {
    fun getMediaCount(mediaType: String, attachments: List<AttachmentViewData>?): Int {
        return attachments?.count { it.type == mediaType } ?: 0
    }

    fun isUnsupportedConversation(conversation: ConversationViewData): Boolean {
        // If conversation state is not defined on client side
        if (!ConversationState.contains(conversation.state)) {
            return true
        }
        //If conversation state is normal and attachment type is not defined on client side
        if (
            conversation.state == STATE_NORMAL &&
            conversation.attachments?.firstOrNull {
                !InternalMediaType.contains(it.type)
            } != null
        ) {
            return true
        }
        return false
    }
}