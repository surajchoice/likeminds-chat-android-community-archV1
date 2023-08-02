package com.likeminds.chatmm.member.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SDKClientInfoViewData private constructor(
    val communityId: Int,
    val user: String,
    val userUniqueId: String,
    val uuid: String
) : Parcelable {
    class Builder {
        private var communityId: Int = 0
        private var user: String = ""
        private var userUniqueId: String = ""
        private var uuid: String = ""

        fun communityId(communityId: Int) = apply { this.communityId = communityId }
        fun user(user: String) = apply { this.user = user }
        fun userUniqueId(userUniqueId: String) = apply { this.userUniqueId = userUniqueId }
        fun uuid(uuid: String) = apply { this.uuid = uuid }

        fun build() = SDKClientInfoViewData(
            communityId,
            user,
            userUniqueId,
            uuid
        )
    }

    fun toBuilder(): Builder {
        return Builder().communityId(communityId)
            .user(user)
            .userUniqueId(userUniqueId)
            .uuid(uuid)
    }
}