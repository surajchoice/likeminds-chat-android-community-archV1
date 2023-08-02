package com.likeminds.chatmm.homefeed.model

import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CONTENT_HEADER_VIEW

class ContentHeaderViewData private constructor(
    val title: String
) : BaseViewType {
    override val viewType: Int
        get() = ITEM_CONTENT_HEADER_VIEW

    class Builder {
        private var title: String = ""

        fun title(title: String) = apply { this.title = title }

        fun build() = ContentHeaderViewData(title)
    }

    fun toBuilder(): Builder {
        return Builder().title(title)
    }
}