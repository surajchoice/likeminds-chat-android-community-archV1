package com.likeminds.chatmm.conversation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class AttachmentMetaViewData private constructor(
    val numberOfPage: Int?,
    val size: Long?,
    val duration: Int?,
) : Parcelable {
    class Builder {
        private var numberOfPage: Int? = null
        private var size: Long? = null
        private var duration: Int? = null

        fun numberOfPage(numberOfPage: Int?) = apply { this.numberOfPage = numberOfPage }
        fun size(size: Long?) = apply { this.size = size }
        fun duration(duration: Int?) = apply { this.duration = duration }

        fun build() = AttachmentMetaViewData(numberOfPage, size, duration)
    }

    fun toBuilder(): Builder {
        return Builder()
            .duration(duration)
            .numberOfPage(numberOfPage)
            .size(size)
    }
}