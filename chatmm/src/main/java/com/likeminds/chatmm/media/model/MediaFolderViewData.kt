package com.likeminds.chatmm.media.model

import android.net.Uri
import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_MEDIA_PICKER_FOLDER
import kotlinx.parcelize.Parcelize

@Parcelize
class MediaFolderViewData private constructor(
    val thumbnailUri: Uri?,
    val title: String,
    val itemCount: Int,
    val bucketId: String,
    val folderType: MediaFolderType
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_FOLDER

    class Builder {
        private var thumbnailUri: Uri? = null
        private var title: String = ""
        private var itemCount: Int = 0
        private var bucketId: String = ""
        private var folderType: MediaFolderType = MediaFolderType.NORMAL

        fun thumbnailUri(thumbnailUri: Uri?) = apply { this.thumbnailUri = thumbnailUri }
        fun title(title: String) = apply { this.title = title }
        fun itemCount(itemCount: Int) = apply { this.itemCount = itemCount }
        fun bucketId(bucketId: String) = apply { this.bucketId = bucketId }
        fun folderType(folderType: MediaFolderType) = apply { this.folderType = folderType }

        fun build() = MediaFolderViewData(
            thumbnailUri,
            title,
            itemCount,
            bucketId,
            folderType
        )
    }

    fun builder(): Builder {
        return Builder().thumbnailUri(thumbnailUri)
            .title(title)
            .itemCount(itemCount)
            .bucketId(bucketId)
            .folderType(folderType)
    }
}