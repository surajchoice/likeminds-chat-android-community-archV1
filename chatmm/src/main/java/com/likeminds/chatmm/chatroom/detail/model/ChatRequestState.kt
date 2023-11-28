package com.likeminds.chatmm.chatroom.detail.model

enum class ChatRequestState(val value: Int?) {
    NOTHING(null),
    INITIATED(0),
    ACCEPTED(1),
    REJECTED(2)
}