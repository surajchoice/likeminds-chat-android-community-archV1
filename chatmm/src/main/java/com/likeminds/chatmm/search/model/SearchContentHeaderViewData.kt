package com.likeminds.chatmm.search.model

import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_SEARCH_CONTENT_HEADER_VIEW

class SearchContentHeaderViewData private constructor(
    val title: String
) : BaseViewType {
    override val viewType: Int
        get() = ITEM_SEARCH_CONTENT_HEADER_VIEW

    class Builder {
        private var title: String = ""

        fun title(title: String) = apply { this.title = title }

        fun build() = SearchContentHeaderViewData(title)
    }
}