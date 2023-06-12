package com.likeminds.chatmm.homefeed.model

import androidx.annotation.Keep
import com.likeminds.likemindschat.community.model.Community
import com.likeminds.likemindschat.user.model.User

@Keep
data class GroupChatResponse(
    val user: User?,
    val community: Community?
)