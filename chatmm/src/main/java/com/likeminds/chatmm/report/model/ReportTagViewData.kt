package com.likeminds.chatmm.report.model

import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_REPORT_TAG
import kotlinx.parcelize.Parcelize

@Parcelize
class ReportTagViewData private constructor(
    val id: Int,
    val name: String,
    val isSelected: Boolean
) : BaseViewType, Parcelable {

    override val viewType: Int
        get() = ITEM_REPORT_TAG

    class Builder {
        private var id: Int = -1
        private var name: String = ""
        private var isSelected: Boolean = false

        fun id(id: Int) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun isSelected(isSelected: Boolean) = apply { this.isSelected = isSelected }

        fun build() = ReportTagViewData(
            id,
            name,
            isSelected
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .name(name)
            .isSelected(isSelected)
    }
}