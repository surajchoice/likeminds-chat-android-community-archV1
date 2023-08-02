package com.likeminds.chatmm.media.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class MediaPickerResult private constructor(
    @MediaPickerResultType val mediaPickerResultType: Int,
    val mediaTypes: List<String>,
    val medias: List<MediaViewData>?,
    val browseClassName: Pair<String, String>?,
    val allowMultipleSelect: Boolean
) : Parcelable {

    class Builder {
        @MediaPickerResultType
        private var mediaPickerResultType: Int = MEDIA_RESULT_BROWSE
        private var mediaTypes: List<String> = emptyList()
        private var medias: List<MediaViewData>? = null
        private var browseClassName: Pair<String, String>? = null
        private var allowMultipleSelect: Boolean = false

        fun mediaPickerResultType(@MediaPickerResultType mediaPickerResultType: Int) =
            apply { this.mediaPickerResultType = mediaPickerResultType }

        fun mediaTypes(mediaTypes: List<String>) = apply { this.mediaTypes = mediaTypes }
        fun medias(medias: List<MediaViewData>?) = apply { this.medias = medias }
        fun browseClassName(browseClassName: Pair<String, String>?) =
            apply { this.browseClassName = browseClassName }

        fun allowMultipleSelect(allowMultipleSelect: Boolean) =
            apply { this.allowMultipleSelect = allowMultipleSelect }

        fun build() = MediaPickerResult(
            mediaPickerResultType,
            mediaTypes,
            medias,
            browseClassName,
            allowMultipleSelect
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .mediaPickerResultType(mediaPickerResultType)
            .mediaTypes(mediaTypes)
            .medias(medias)
            .browseClassName(browseClassName)
            .allowMultipleSelect(allowMultipleSelect)
    }
}