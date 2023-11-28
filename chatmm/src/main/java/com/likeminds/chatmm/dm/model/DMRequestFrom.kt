package com.likeminds.chatmm.dm.model

enum class DMRequestFrom(val value: String) {
    MEMBER_PROFILE("member_profile"),
    CHATROOM("chatroom"),
    DM_FEED("dm_feed_v2")
}