package com.likeminds.chatmm.media.model

import android.net.Uri
import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_IMAGE_SWIPE
import kotlinx.parcelize.Parcelize

@Parcelize
class MediaSwipeViewData private constructor(
    val uri: Uri,
    val thumbnail: String?,
    val dynamicViewType: Int?,
    val index: Int,
    val title: String?,
    val subTitle: String?,
    val type: String?,
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = dynamicViewType ?: ITEM_IMAGE_SWIPE

    class Builder {
        private var uri: Uri = Uri.EMPTY
        private var thumbnail: String? = null
        private var dynamicViewType: Int? = null
        private var index: Int = -1
        private var title: String? = null
        private var subTitle: String? = null
        private var type: String? = null

        fun uri(uri: Uri) = apply { this.uri = uri }
        fun thumbnail(thumbnail: String?) = apply { this.thumbnail = thumbnail }
        fun dynamicViewType(dynamicViewType: Int?) =
            apply { this.dynamicViewType = dynamicViewType }

        fun index(index: Int) = apply { this.index = index }
        fun title(title: String?) = apply { this.title = title }
        fun subTitle(subTitle: String?) = apply { this.subTitle = subTitle }
        fun type(type: String?) = apply { this.type = type }

        fun build() = MediaSwipeViewData(
            uri,
            thumbnail,
            dynamicViewType,
            index,
            title,
            subTitle,
            type
        )
    }

    fun toBuilder(): Builder {
        return Builder().uri(uri)
            .thumbnail(thumbnail)
            .dynamicViewType(dynamicViewType)
            .index(index)
            .title(title)
            .subTitle(subTitle)
            .type(type)
    }
}