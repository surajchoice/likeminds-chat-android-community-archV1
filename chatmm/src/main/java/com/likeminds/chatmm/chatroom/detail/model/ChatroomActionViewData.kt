package com.likeminds.chatmm.chatroom.detail.model

import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_NONE
import kotlinx.parcelize.Parcelize

@Parcelize
class ChatroomActionViewData private constructor(
    var id: String,
    var title: String,
    var route: String?,
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_NONE

    class Builder {
        private var id: String = ""
        private var title: String = ""
        private var route: String? = null

        fun id(id: String) = apply { this.id = id }
        fun title(title: String) = apply { this.title = title }
        fun route(route: String?) = apply { this.route = route }


        fun build() = ChatroomActionViewData(id, title, route)
    }

    fun toBuilder(): Builder {
        return Builder().id(id).title(title).route(route)
    }
}