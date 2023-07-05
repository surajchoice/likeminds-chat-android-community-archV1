package com.likeminds.chatmm.conversation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LinkOGTagsViewData private constructor(
    val title: String?,
    val image: String?,
    val description: String?,
    val url: String?,
) : Parcelable {
    class Builder {
        private var title: String? = null
        private var image: String? = null
        private var description: String? = null
        private var url: String? = null

        fun title(title: String?) = apply { this.title = title }
        fun image(image: String?) = apply { this.image = image }
        fun description(description: String?) = apply { this.description = description }
        fun url(url: String?) = apply { this.url = url }

        fun build() = LinkOGTagsViewData(
            title,
            image,
            description,
            url
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .title(title)
            .image(image)
            .description(description)
            .url(url)
    }
}