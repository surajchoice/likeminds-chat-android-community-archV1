package com.likeminds.chatmm.media.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class VideoTrimExtras private constructor(
    val initiatedSendMessage: Boolean?,
    val newMediaSelected: Boolean?,
    val updatedMediaPosition: Int?,
    val updatedMediaData: Int?,
) : Parcelable {
}