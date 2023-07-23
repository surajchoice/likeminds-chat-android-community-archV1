package com.likeminds.chatmm.media.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SingleUriData private constructor(
    val uri: Uri,
    val fileType: String,
    val width: Int?,
    val height: Int?,
    val thumbnailUri: Uri?,
    val size: Long,
    val mediaName: String?,
    val pdfPageCount: Int?,
    val duration: Int?,
    val awsFolderPath: String?,
    val localFilePath: String?,
    val format: String?
) : Parcelable {
    class Builder {
        private var uri: Uri = Uri.parse("")
        private var fileType: String = ""
        private var width: Int? = null
        private var height: Int? = null
        private var thumbnailUri: Uri? = null
        private var size: Long = 0
        private var mediaName: String? = null
        private var pdfPageCount: Int? = null
        private var duration: Int? = null
        private var awsFolderPath: String? = null
        private var localFilePath: String? = null
        private var format: String? = null

        fun uri(uri: Uri) = apply { this.uri = uri }
        fun fileType(fileType: String) = apply { this.fileType = fileType }
        fun width(width: Int?) = apply { this.width = width }
        fun height(height: Int?) = apply { this.height = height }
        fun thumbnailUri(thumbnailUri: Uri?) = apply { this.thumbnailUri = thumbnailUri }
        fun size(size: Long) = apply { this.size = size }
        fun mediaName(mediaName: String?) = apply { this.mediaName = mediaName }
        fun pdfPageCount(pdfPageCount: Int?) = apply { this.pdfPageCount = pdfPageCount }
        fun duration(duration: Int?) = apply { this.duration = duration }
        fun awsFolderPath(awsFolderPath: String?) = apply { this.awsFolderPath = awsFolderPath }
        fun localFilePath(localFilePath: String?) = apply { this.localFilePath = localFilePath }
        fun format(format: String?) = apply { this.format = format }


        fun build() = SingleUriData(
            uri,
            fileType,
            width,
            height,
            thumbnailUri,
            size,
            mediaName,
            pdfPageCount,
            duration,
            awsFolderPath,
            localFilePath,
            format
        )
    }

    fun toBuilder(): Builder {
        return Builder().uri(uri)
            .fileType(fileType)
            .width(width)
            .height(height)
            .thumbnailUri(thumbnailUri)
            .size(size)
            .mediaName(mediaName)
            .pdfPageCount(pdfPageCount)
            .duration(duration)
            .awsFolderPath(awsFolderPath)
            .localFilePath(localFilePath)
            .format(format)
    }
}