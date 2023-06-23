package com.likeminds.chatmm.chatroom.detail.model

import androidx.annotation.IntDef

const val CHAT_BOX_NORMAL = 1
const val CHAT_BOX_REPLY = 2
const val CHAT_BOX_LINK = 3
const val CHAT_BOX_INTERNAL_LINK = 4

@IntDef(
    CHAT_BOX_NORMAL,
    CHAT_BOX_REPLY,
    CHAT_BOX_LINK,
    CHAT_BOX_INTERNAL_LINK
)
@Retention(AnnotationRetention.SOURCE)
internal annotation class ChatBoxType