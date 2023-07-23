package com.likeminds.chatmm.chatroom.detail.model

import androidx.annotation.IntDef

const val REPLY_SOURCE_CHATROOM = 1
const val REPLY_SOURCE_CONVERSATION = 2

@IntDef(
    REPLY_SOURCE_CHATROOM,
    REPLY_SOURCE_CONVERSATION
)
@Retention(AnnotationRetention.SOURCE)
internal annotation class ReplySourceType