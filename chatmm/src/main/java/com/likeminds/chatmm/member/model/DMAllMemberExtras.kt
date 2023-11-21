package com.likeminds.chatmm.member.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DMAllMemberExtras private constructor(
    val showList: Int
) : Parcelable {

    class Builder {

        private var showList: Int = -1

        fun showList(showList: Int) = apply { this.showList = showList }

        fun build() = DMAllMemberExtras(showList)
    }

    fun toBuilder(): Builder {
        return Builder().showList(showList)
    }
}