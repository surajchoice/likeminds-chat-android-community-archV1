package com.likeminds.chatmm.chatroom.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class MemberViewData private constructor(

) : Parcelable {
    class Builder {
        fun build() = MemberViewData()
    }

    fun toBuilder(): Builder {
        return Builder()
    }
}