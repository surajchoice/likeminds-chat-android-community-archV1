package com.likeminds.chatmm.media.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class MediaPickerResult private constructor(
    val isResultOk: Boolean,
    @MediaPickerResultType var mediaPickerResultType: Int,
    val mediaTypes: List<String>,
    val medias: List<MediaViewData>?,
    val browseClassName: Pair<String, String>?,
    val allowMultipleSelect: Boolean,
) : Parcelable {

    class Builder {
        private var isResultOk: Boolean = false

        @MediaPickerResultType
        private var mediaPickerResultType: Int = MEDIA_RESULT_BROWSE
        private var mediaTypes: List<String> = emptyList()
        private var medias: List<MediaViewData>? = null
        private var browseClassName: Pair<String, String>? = null
        private var allowMultipleSelect: Boolean = false

        fun isResultOk(isResultOk: Boolean) = apply { this.isResultOk = isResultOk }
        fun mediaPickerResultType(@MediaPickerResultType mediaPickerResultType: Int) =
            apply { this.mediaPickerResultType = mediaPickerResultType }

        fun mediaTypes(mediaTypes: List<String>) = apply { this.mediaTypes = mediaTypes }
        fun medias(medias: List<MediaViewData>?) = apply { this.medias = medias }
        fun browseClassName(browseClassName: Pair<String, String>?) =
            apply { this.browseClassName = browseClassName }

        fun allowMultipleSelect(allowMultipleSelect: Boolean) =
            apply { this.allowMultipleSelect = allowMultipleSelect }

        fun build() = MediaPickerResult(
            isResultOk,
            mediaPickerResultType,
            mediaTypes,
            medias,
            browseClassName,
            allowMultipleSelect
        )
    }

    fun toBuilder(): Builder {
        return Builder().isResultOk(isResultOk)
            .mediaPickerResultType(mediaPickerResultType)
            .mediaTypes(mediaTypes)
            .medias(medias)
            .browseClassName(browseClassName)
            .allowMultipleSelect(allowMultipleSelect)
    }
}