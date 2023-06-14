package com.likeminds.chatmm.chatroom.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ChatroomDetailExtras private constructor(

) : Parcelable {
    class Builder {
        fun build() = ChatroomDetailExtras()
    }

    fun toBuilder(): Builder {
        return Builder()
    }
}