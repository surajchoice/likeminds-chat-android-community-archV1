package com.likeminds.chatmm.member.model

import com.likeminds.likemindschat.community.model.Community
import com.likeminds.likemindschat.user.model.User


data class UserResponse(
    val user: User?, //user data
    val community: Community?, //community data
    val appAccess: Boolean?,
    val accessToken: String? = null,
    val refreshToken: String? = null
)
