package com.likeminds.chatmm.dm.model

import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_DM_FEED_EMPTY_DATA

class DMFeedEmptyViewData private constructor() : BaseViewType {

    override val viewType: Int
        get() = ITEM_DM_FEED_EMPTY_DATA

    class Builder {
        fun build() = DMFeedEmptyViewData()
    }
}