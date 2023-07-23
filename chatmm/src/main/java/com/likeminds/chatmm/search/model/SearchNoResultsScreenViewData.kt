package com.likeminds.chatmm.search.model

import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_NO_SEARCH_RESULTS_VIEW

class SearchNoResultsScreenViewData private constructor() : BaseViewType {
    override val viewType: Int
        get() = ITEM_NO_SEARCH_RESULTS_VIEW

    class Builder {
        fun build() = SearchNoResultsScreenViewData()
    }
}