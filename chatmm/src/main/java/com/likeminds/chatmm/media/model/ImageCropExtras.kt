package com.likeminds.chatmm.media.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ImageCropExtras private constructor(
    val singleUriData: SingleUriData?
) : Parcelable {
    internal class Builder {
        private var singleUriData: SingleUriData? = null
        private var cropSquare: Boolean = false

        fun singleUriData(singleUriData: SingleUriData?) =
            apply { this.singleUriData = singleUriData }
    }
}