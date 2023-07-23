
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
}