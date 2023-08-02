package com.likeminds.chatmm.polls.model

import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_POLL_MORE_OPTIONS
import kotlinx.parcelize.Parcelize

@Parcelize
class PollMoreOptionsViewData private constructor(
    val optionsCount: Int
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_POLL_MORE_OPTIONS

    class Builder {
        private var optionsCount: Int = -1

        fun optionsCount(optionsCount: Int) = apply { this.optionsCount = optionsCount }

        fun build() = PollMoreOptionsViewData(optionsCount)
    }

    fun toBuilder(): Builder {
        return Builder().optionsCount(optionsCount)
    }
}