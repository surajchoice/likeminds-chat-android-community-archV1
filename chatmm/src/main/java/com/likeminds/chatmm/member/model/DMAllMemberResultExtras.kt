package com.likeminds.chatmm.member.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DMAllMemberResultExtras private constructor(
    val chatroomId: String
) : Parcelable {

    class Builder {

        private var chatroomId: String = ""

        fun chatroomId(chatroomId: String) = apply { this.chatroomId = chatroomId }

        fun build() = DMAllMemberResultExtras(chatroomId)
    }

    fun toBuilder(): Builder {
        return Builder().chatroomId(chatroomId)
    }
}