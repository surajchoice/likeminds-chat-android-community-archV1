package com.likeminds.chatmm.chatroom.detail.model

import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_MEMBER_ACTION
import kotlinx.parcelize.Parcelize

@Parcelize
class MemberActionViewData private constructor(
    var title: String,
    var route: String,
    var hideBottomLine: Boolean?
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_MEMBER_ACTION

    class Builder {
        private var title: String = ""
        private var route: String = ""
        private var hideBottomLine: Boolean? = null

        fun title(title: String) = apply { this.title = title }
        fun route(route: String) = apply { this.route = route }
        fun hideBottomLine(hideBottomLine: Boolean?) =
            apply { this.hideBottomLine = hideBottomLine }

        fun build() = MemberActionViewData(title, route, hideBottomLine)
    }

    fun toBuilder(): Builder {
        return Builder().title(title)
            .route(route)
            .hideBottomLine(hideBottomLine)
    }
}