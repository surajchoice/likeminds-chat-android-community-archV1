package com.likeminds.chatmm.media.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class MediaPickerItemExtras private constructor(
    var bucketId: String,
    var folderTitle: String,
    var mediaTypes: List<String>,
    var allowMultipleSelect: Boolean,
) : Parcelable {
    class Builder {
        private var bucketId: String = ""
        private var folderTitle: String = ""
        private var mediaTypes: List<String> = emptyList()
        private var allowMultipleSelect: Boolean = true

        fun bucketId(bucketId: String) = apply { this.bucketId = bucketId }
        fun folderTitle(folderTitle: String) = apply { this.folderTitle = folderTitle }
        fun mediaTypes(mediaTypes: List<String>) = apply { this.mediaTypes = mediaTypes }
        fun allowMultipleSelect(allowMultipleSelect: Boolean) =
            apply { this.allowMultipleSelect = allowMultipleSelect }

        fun build() = MediaPickerItemExtras(bucketId, folderTitle, mediaTypes, allowMultipleSelect)
    }

    fun toBuilder(): Builder {
        return Builder().bucketId(bucketId)
            .folderTitle(folderTitle)
            .mediaTypes(mediaTypes)
            .allowMultipleSelect(allowMultipleSelect)
    }
}