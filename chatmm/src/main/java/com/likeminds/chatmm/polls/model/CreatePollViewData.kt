package com.likeminds.chatmm.polls.model

import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CREATE_POLL
import kotlinx.parcelize.Parcelize

@Parcelize
class CreatePollViewData private constructor(
    val text: String?,
    val subText: String?
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_CREATE_POLL

    class Builder {
        private var text: String? = ""
        private var subText: String? = ""

        fun text(text: String?) = apply { this.text = text }
        fun subText(subText: String?) = apply { this.subText = subText }

        fun build() = CreatePollViewData(text, subText)
    }

    fun toBuilder(): Builder {
        return Builder().text(text).subText(subText)
    }
}