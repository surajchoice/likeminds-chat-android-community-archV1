package com.likeminds.chatmm.homefeed.model

import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_HOME_LINE_BREAK_VIEW

class HomeLineBreakViewData private constructor() : BaseViewType {
    override val viewType: Int
        get() = ITEM_HOME_LINE_BREAK_VIEW

    class Builder {
        fun build() = HomeLineBreakViewData()
    }
}