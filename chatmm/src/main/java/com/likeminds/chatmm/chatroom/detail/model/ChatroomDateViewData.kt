package com.likeminds.chatmm.chatroom.detail.model

import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CHATROOM_DATE
import kotlinx.parcelize.Parcelize

@Parcelize
class ChatroomDateViewData private constructor(
    var date: String?,
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_CHATROOM_DATE

    class Builder {
        private var date: String? = null

        fun date(date: String?) = apply { this.date = date }

        fun build() = ChatroomDateViewData(date)
    }

    fun toBuilder(): Builder {
        return Builder().date(date)
    }
}