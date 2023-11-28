package com.likeminds.chatmm.dm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CheckDMTabViewData private constructor(
    val hideDMTab: Boolean,
    val hideDMText: String?,
    val isCM: Boolean,
    val unreadDMCount: Int
) : Parcelable {
    class Builder {
        private var hideDMTab: Boolean = false
        private var hideDMText: String? = null
        private var isCM: Boolean = false
        private var unreadDMCount: Int = 0

        fun hideDMTab(hideDMTab: Boolean) = apply { this.hideDMTab = hideDMTab }
        fun hideDMText(hideDMText: String?) = apply { this.hideDMText = hideDMText }
        fun isCM(isCM: Boolean) = apply { this.isCM = isCM }
        fun unreadDMCount(unreadDMCount: Int) = apply { this.unreadDMCount = unreadDMCount }
    }

    fun toBuilder(): Builder {
        return Builder().hideDMTab(hideDMTab)
            .hideDMText(hideDMText)
            .isCM(isCM)
            .unreadDMCount(unreadDMCount)
    }
}