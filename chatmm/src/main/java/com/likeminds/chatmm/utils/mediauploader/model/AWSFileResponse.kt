package com.likeminds.chatmm.utils.mediauploader.model

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver

class AWSFileResponse private constructor(
    val name: String,
    val awsFolderPath: String,
    val transferObserver: TransferObserver?,
    val index: Int,
    val fileType: Int,
    val width: Int?,
    val height: Int?,
    val isThumbnail: Boolean?,
    val hasThumbnail: Boolean?,
    val pageCount: Int?,
    val size: Long?,
    val duration: Int?,
    val uuid: String?
) {
    class Builder {

        private var name: String = ""
        private var awsFolderPath: String = ""
        private var transferObserver: TransferObserver? = null
        private var index: Int = 0
        private var fileType: Int = 0
        private var width: Int? = null
        private var height: Int? = null
        private var isThumbnail: Boolean? = null
        private var hasThumbnail: Boolean? = null
        private var pageCount: Int? = null
        private var size: Long? = null
        private var duration: Int? = null
        private var uuid: String? = null

        fun name(name: String) = apply { this.name = name }
        fun awsFolderPath(awsFolderPath: String) = apply { this.awsFolderPath = awsFolderPath }
        fun transferObserver(transferObserver: TransferObserver?) =
            apply { this.transferObserver = transferObserver }

        fun index(index: Int) = apply { this.index = index }
        fun fileType(fileType: Int) = apply { this.fileType = fileType }
        fun width(width: Int?) = apply { this.width = width }
        fun height(height: Int?) = apply { this.height = height }
        fun isThumbnail(isThumbnail: Boolean?) = apply { this.isThumbnail = isThumbnail }
        fun hasThumbnail(hasThumbnail: Boolean?) = apply { this.hasThumbnail = hasThumbnail }
        fun pageCount(pageCount: Int?) = apply { this.pageCount = pageCount }
        fun size(size: Long?) = apply { this.size = size }
        fun duration(duration: Int?) = apply { this.duration = duration }
        fun uuid(uuid: String?) = apply { this.uuid = uuid }

        fun build() = AWSFileResponse(
            name,
            awsFolderPath,
            transferObserver,
            index,
            fileType,
            width,
            height,
            isThumbnail,
            hasThumbnail,
            pageCount,
            size,
            duration,
            uuid
        )
    }

    fun toBuilder(): Builder {
        return Builder().name(name)
            .awsFolderPath(awsFolderPath)
            .transferObserver(transferObserver)
            .index(index)
            .fileType(fileType)
            .width(width)
            .height(height)
            .isThumbnail(isThumbnail)
            .hasThumbnail(hasThumbnail)
            .pageCount(pageCount)
            .size(size)
            .duration(duration)
            .uuid(uuid)
    }
}