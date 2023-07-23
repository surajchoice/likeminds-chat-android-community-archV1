package com.likeminds.chatmm.search.model

import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_SEARCH_LINE_BREAK_VIEW

class SearchLineBreakViewData private constructor() : BaseViewType {
    override val viewType: Int
        get() = ITEM_SEARCH_LINE_BREAK_VIEW

    class Builder {
        fun build() = SearchLineBreakViewData()
    }
}