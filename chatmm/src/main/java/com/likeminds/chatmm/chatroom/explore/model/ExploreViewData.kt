package com.likeminds.chatmm.chatroom.explore.model

import android.os.Parcelable
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_EXPLORE
import kotlinx.parcelize.Parcelize

@Parcelize
class ExploreViewData private constructor(
    val isPinned: Boolean?,
    val isCreator: Boolean,
    val externalSeen: Boolean?,
    val isSecret: Boolean?,
    var followStatus: Boolean?,
    val participantsCount: Int?,
    val totalResponseCount: Int?,
    val sortIndex: Int,
    val id: String,
    val header: String?,
    val title: String?,
    val imageUrl: String?,
    val chatroomViewData: ChatroomViewData?,
    val chatroomImageUrl: String?
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = ITEM_EXPLORE

    class Builder {
        private var isPinned: Boolean? = false
        private var isCreator: Boolean = false
        private var externalSeen: Boolean? = null
        private var isSecret: Boolean? = null
        private var followStatus: Boolean? = null
        private var participantsCount: Int? = null
        private var totalResponseCount: Int? = null
        private var sortIndex: Int = 0
        private var id: String = ""
        private var header: String? = null
        private var title: String? = null
        private var imageUrl: String? = null
        private var chatroomViewData: ChatroomViewData? = null
        private var chatroomImageUrl: String? = null

        fun isPinned(isPinned: Boolean?) = apply { this.isPinned = isPinned }

        fun isCreator(isCreator: Boolean) = apply { this.isCreator = isCreator }

        fun externalSeen(externalSeen: Boolean?) = apply { this.externalSeen = externalSeen }

        fun isSecret(isSecret: Boolean?) = apply { this.isSecret = isSecret }

        fun followStatus(followStatus: Boolean?) = apply { this.followStatus = followStatus }

        fun participantsCount(participantsCount: Int?) = apply {
            this.participantsCount = participantsCount
        }

        fun totalResponseCount(totalResponseCount: Int?) = apply {
            this.totalResponseCount = totalResponseCount
        }

        fun sortIndex(sortIndex: Int) = apply {
            this.sortIndex = sortIndex
        }

        fun id(id: String) = apply { this.id = id }

        fun header(header: String?) = apply { this.header = header }

        fun title(title: String?) = apply { this.title = title }

        fun imageUrl(imageUrl: String?) = apply { this.imageUrl = imageUrl }

        fun chatroomViewData(chatroomViewData: ChatroomViewData?) =
            apply { this.chatroomViewData = chatroomViewData }

        fun chatroomImageUrl(chatroomImageUrl: String?) =
            apply { this.chatroomImageUrl = chatroomImageUrl }

        fun build() = ExploreViewData(
            isPinned,
            isCreator,
            externalSeen,
            isSecret,
            followStatus,
            participantsCount,
            totalResponseCount,
            sortIndex,
            id,
            header,
            title,
            imageUrl,
            chatroomViewData,
            chatroomImageUrl
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .isPinned(isPinned)
            .isCreator(isCreator)
            .externalSeen(externalSeen)
            .isSecret(isSecret)
            .followStatus(followStatus)
            .participantsCount(participantsCount)
            .totalResponseCount(totalResponseCount)
            .sortIndex(sortIndex)
            .id(id)
            .header(header)
            .title(title)
            .imageUrl(imageUrl)
            .chatroomViewData(chatroomViewData)
            .chatroomImageUrl(chatroomImageUrl)
    }
}