package com.likeminds.chatmm.chatroom.detail.model

import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_MANAGEMENT_RIGHT_PERMISSION
import kotlinx.parcelize.Parcelize

@Parcelize
class ManagementRightPermissionViewData private constructor(
    val id: Int,
    val state: Int?,
    val title: String,
    val subtitle: String?,
    val isSelected: Boolean,
    val isLocked: Boolean?,
    val hideBottomLine: Boolean?
) : BaseViewType, Parcelable {
    override val viewType: Int
        get() = ITEM_MANAGEMENT_RIGHT_PERMISSION

    class Builder {
        private var id: Int = 0
        private var state: Int? = null
        private var title: String = ""
        private var subtitle: String? = null
        private var isSelected: Boolean = false
        private var isLocked: Boolean? = null
        private var hideBottomLine: Boolean? = false

        fun id(id: Int) = apply { this.id = id }
        fun state(state: Int?) = apply { this.state = state }
        fun title(title: String) = apply { this.title = title }
        fun subtitle(subtitle: String?) = apply { this.subtitle = subtitle }
        fun isSelected(isSelected: Boolean) = apply { this.isSelected = isSelected }
        fun isLocked(isLocked: Boolean?) = apply { this.isLocked = isLocked }
        fun hideBottomLine(hideBottomLine: Boolean?) =
            apply { this.hideBottomLine = hideBottomLine }

        fun build() = ManagementRightPermissionViewData(
            id,
            state,
            title,
            subtitle,
            isSelected,
            isLocked,
            hideBottomLine
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .state(state)
            .title(title)
            .subtitle(subtitle)
            .isSelected(isSelected)
            .isLocked(isLocked)
            .hideBottomLine(hideBottomLine)
    }
}