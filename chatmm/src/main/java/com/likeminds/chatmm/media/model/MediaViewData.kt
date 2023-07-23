package com.likeminds.chatmm.media.model

import android.net.Uri
import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_MEDIA_PICKER_SINGLE
import kotlinx.parcelize.Parcelize

@Parcelize
class MediaViewData private constructor(
    val uri: Uri,
    val mimeType: String?,
    val mediaType: String,
    val date: Long,
    val size: Long,
    val duration: Int?,
    val bucketId: String?,
    val dateTimeStampHeader: String,
    val mediaName: String?,
    val dynamicViewType: Int?,
    val filteredKeywords: List<String>?,
    val pdfPageCount: Int?,
    val playOrPause: String?,
    val audioProgress: Int?,
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = dynamicViewType ?: ITEM_MEDIA_PICKER_SINGLE

    class Builder {
        private var uri: Uri = Uri.parse("")
        private var mimeType: String? = null
        private var mediaType: String = ""
        private var date: Long = 0
        private var size: Long = 0
        private var duration: Int? = null
        private var bucketId: String? = null
        private var dateTimeStampHeader: String = ""
        private var mediaName: String? = null
        private var dynamicViewType: Int? = null
        private var filteredKeywords: List<String>? = null
        private var pdfPageCount: Int? = null
        private var playOrPause: String? = null
        private var audioProgress: Int? = null

        fun uri(uri: Uri) = apply { this.uri = uri }
        fun mimeType(mimeType: String?) = apply { this.mimeType = mimeType }
        fun mediaType(mediaType: String) = apply { this.mediaType = mediaType }
        fun date(date: Long) = apply { this.date = date }
        fun size(size: Long) = apply { this.size = size }
        fun duration(duration: Int?) = apply { this.duration = duration }
        fun bucketId(bucketId: String?) = apply { this.bucketId = bucketId }
        fun dateTimeStampHeader(dateTimeStampHeader: String) =
            apply { this.dateTimeStampHeader = dateTimeStampHeader }

        fun mediaName(mediaName: String?) = apply { this.mediaName = mediaName }
        fun dynamicViewType(dynamicViewType: Int?) =
            apply { this.dynamicViewType = dynamicViewType }

        fun filteredKeywords(filteredKeywords: List<String>?) =
            apply { this.filteredKeywords = filteredKeywords }

        fun pdfPageCount(pdfPageCount: Int?) = apply { this.pdfPageCount = pdfPageCount }
        fun playOrPause(playOrPause: String?) = apply { this.playOrPause = playOrPause }
        fun audioProgress(audioProgress: Int?) = apply { this.audioProgress = audioProgress }

        fun build() = MediaViewData(
            uri,
            mimeType,
            mediaType,
            date,
            size,
            duration,
            bucketId,
            dateTimeStampHeader,
            mediaName,
            dynamicViewType,
            filteredKeywords,
            pdfPageCount,
            playOrPause,
            audioProgress
        )
    }

    fun toBuilder(): Builder {
        return Builder().uri(uri)
            .mimeType(mimeType)
            .mediaType(mediaType)
            .date(date)
            .size(size)
            .duration(duration)
            .bucketId(bucketId)
            .dateTimeStampHeader(dateTimeStampHeader)
            .mediaName(mediaName)
            .dynamicViewType(dynamicViewType)
            .filteredKeywords(filteredKeywords)
            .pdfPageCount(pdfPageCount)
            .playOrPause(playOrPause)
            .audioProgress(audioProgress)
    }
}