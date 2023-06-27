package com.likeminds.chatmm.media.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class VideoTrimExtras private constructor(
    val initiatedSendMessage: Boolean?,
    val newMediaSelected: Boolean?,
    val updatedMediaPosition: Int?,
    val updatedMediaData: SmallMediaViewData?
) : Parcelable {
    class Builder {
        private var initiatedSendMessage: Boolean? = null
        private var newMediaSelected: Boolean? = null
        private var updatedMediaPosition: Int? = null
        private var updatedMediaData: SmallMediaViewData? = null

        fun initiatedSendMessage(initiatedSendMessage: Boolean?) =
            apply { this.initiatedSendMessage = initiatedSendMessage }

        fun newMediaSelected(newMediaSelected: Boolean?) =
            apply { this.newMediaSelected = newMediaSelected }

        fun updatedMediaPosition(updatedMediaPosition: Int?) =
            apply { this.updatedMediaPosition = updatedMediaPosition }

        fun updatedMediaData(updatedMediaData: SmallMediaViewData?) =
            apply { this.updatedMediaData = updatedMediaData }

        fun build() = VideoTrimExtras(
            initiatedSendMessage,
            newMediaSelected,
            updatedMediaPosition,
            updatedMediaData
        )
    }

    fun toBuilder(): Builder {
        return Builder().initiatedSendMessage(initiatedSendMessage)
            .newMediaSelected(newMediaSelected)
            .updatedMediaPosition(updatedMediaPosition)
            .updatedMediaData(updatedMediaData)
    }
}