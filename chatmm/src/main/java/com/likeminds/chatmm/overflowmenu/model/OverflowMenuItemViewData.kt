package com.likeminds.chatmm.overflowmenu.model

import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_OVERFLOW_MENU_ITEM

class OverflowMenuItemViewData private constructor(
    val title: String,
    val badge: Int,
    val route: String,
    val showWarning: Boolean
) : BaseViewType {
    override val viewType: Int
        get() = ITEM_OVERFLOW_MENU_ITEM

    fun badge(): String {
        return if (badge > 9) "9+" else badge.toString()
    }

    class Builder {
        private var title: String = ""
        private var badge: Int = 0
        private var route: String = ""
        private var showWarning: Boolean = false

        fun title(title: String) = apply { this.title = title }

        fun badge(badge: Int) = apply { this.badge = badge }

        fun route(route: String) = apply { this.route = route }

        fun showWarning(showWarning: Boolean) = apply { this.showWarning = showWarning }

        fun build() = OverflowMenuItemViewData(title, badge, route, showWarning)
    }

    fun toBuilder(): Builder {
        return Builder()
            .title(title)
            .badge(badge)
            .route(route)
            .showWarning(showWarning)
    }
}