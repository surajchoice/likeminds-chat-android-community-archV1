package com.likeminds.chatmm.utils.membertagging.model

import android.graphics.drawable.Drawable
import com.likeminds.chatmm.member.model.SDKClientInfoViewData

class TagViewData private constructor(
    val name: String,
    val imageUrl: String,
    val id: Int,
    val isGuest: Boolean,
    val userUniqueId: String,
    val placeholder: Drawable?,
    val route: String,
    val tag: String,
    val description: String,
    val isLastItem: Boolean,
    val sdkClientInfo: SDKClientInfoViewData
) {
    class Builder {
        private var name: String = ""
        private var imageUrl: String = ""
        private var id: Int = 0
        private var isGuest: Boolean = false
        private var userUniqueId: String = ""
        private var placeholder: Drawable? = null
        private var route: String = ""
        private var tag: String = ""
        private var description: String = ""
        private var isLastItem: Boolean = false
        private var sdkClientInfo: SDKClientInfoViewData = SDKClientInfoViewData.Builder().build()

        fun name(name: String) = apply { this.name = name }
        fun imageUrl(imageUrl: String) = apply { this.imageUrl = imageUrl }
        fun id(id: Int) = apply { this.id = id }
        fun isGuest(isGuest: Boolean) = apply { this.isGuest = isGuest }
        fun userUniqueId(userUniqueId: String) = apply { this.userUniqueId = userUniqueId }
        fun placeHolder(placeholder: Drawable?) = apply { this.placeholder = placeholder }
        fun route(route: String) = apply { this.route = route }
        fun tag(tag: String) = apply { this.tag = tag }
        fun description(description: String) = apply { this.description = description }
        fun isLastItem(isLastItem: Boolean) = apply { this.isLastItem = isLastItem }
        fun sdkClientInfo(sdkClientInfo: SDKClientInfoViewData) =
            apply { this.sdkClientInfo = sdkClientInfo }

        fun build() = TagViewData(
            name,
            imageUrl,
            id,
            isGuest,
            userUniqueId,
            placeholder,
            route,
            tag,
            description,
            isLastItem,
            sdkClientInfo
        )
    }

    fun toBuilder(): Builder {
        return Builder().name(name)
            .imageUrl(imageUrl)
            .id(id)
            .isGuest(isGuest)
            .userUniqueId(userUniqueId)
            .placeHolder(placeholder)
            .route(route)
            .tag(tag)
            .description(description)
            .isLastItem(isLastItem)
            .sdkClientInfo(sdkClientInfo)
    }
}