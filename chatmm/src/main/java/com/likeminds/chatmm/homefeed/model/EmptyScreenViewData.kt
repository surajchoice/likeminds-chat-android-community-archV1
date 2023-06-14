package com.likeminds.chatmm.homefeed.model

import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_EMPTY_VIEW

class EmptyScreenViewData private constructor(
    val title: String,
    val subTitle: String
) : BaseViewType {
    override val viewType: Int
        get() = ITEM_EMPTY_VIEW

    class Builder {
        private var title: String = ""
        private var subTitle: String = ""

        fun title(title: String) = apply { this.title = title }
        fun subTitle(subTitle: String) = apply { this.subTitle = subTitle }

        fun build() = EmptyScreenViewData(title, subTitle)
    }

    fun toBuilder(): Builder {
        return Builder().subTitle(subTitle)
            .title(title)
    }
}