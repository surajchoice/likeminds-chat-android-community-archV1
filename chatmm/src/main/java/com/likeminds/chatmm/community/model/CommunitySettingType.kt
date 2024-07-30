package com.likeminds.chatmm.community.model

enum class CommunitySettingType(val value: String) {
    SECRET_CHATROOM_INVITE("secret_chatrooms_invite"),
    DIRECT_MESSAGING("direct_messages_setting"),
    DM_M2CM("direct_messages"),
    DN_M2M("members_can_dm"),
}