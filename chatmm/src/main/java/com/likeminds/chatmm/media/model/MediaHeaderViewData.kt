package com.likeminds.chatmm.media.model

import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_MEDIA_PICKER_HEADER
import kotlinx.parcelize.Parcelize

@Parcelize
class MediaHeaderViewData private constructor(
    val title: String
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_HEADER

    class Builder {
        private var title: String = ""

        fun title(title: String) = apply { this.title = title }

        fun build() = MediaHeaderViewData(title)
    }

    fun toBuilder(): Builder {
        return Builder().title(title)
    }
}