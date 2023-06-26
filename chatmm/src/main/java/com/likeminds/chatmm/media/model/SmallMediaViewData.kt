package com.likeminds.chatmm.media.model

import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_MEDIA_SMALL
import kotlinx.parcelize.Parcelize

@Parcelize
class SmallMediaViewData private constructor(
    val singleUriData: SingleUriData?,
    val isSelected: Boolean,
    val dynamicViewType: Int?
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = dynamicViewType ?: ITEM_MEDIA_SMALL

    class Builder {
        private var singleUriData: SingleUriData? = null
        private var isSelected: Boolean = false
        private var dynamicViewType: Int? = null

        fun singleUriData(singleUriData: SingleUriData?) =
            apply { this.singleUriData = singleUriData }

        fun isSelected(isSelected: Boolean) = apply { this.isSelected = isSelected }
        fun dynamicViewType(dynamicViewType: Int?) =
            apply { this.dynamicViewType = dynamicViewType }

        fun build() = SmallMediaViewData(
            singleUriData,
            isSelected,
            dynamicViewType
        )
    }

    fun toBuilder(): Builder {
        return Builder().singleUriData(singleUriData)
            .isSelected(isSelected)
            .dynamicViewType(dynamicViewType)
    }
}