package com.likeminds.chatmm.search.model

import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_SINGLE_SHIMMER

class SingleShimmerViewData private constructor() : BaseViewType {
    override val viewType: Int
        get() = ITEM_SINGLE_SHIMMER

    class Builder {
        fun build() = SingleShimmerViewData()
    }
}