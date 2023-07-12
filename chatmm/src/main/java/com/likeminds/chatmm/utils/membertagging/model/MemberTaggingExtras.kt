package com.collabmates.membertagging.model

import android.widget.EditText
import androidx.annotation.FloatRange

class MemberTaggingExtras(
    val editText: EditText,
    val darkMode: Boolean = false,
    @FloatRange(from = 0.0, to = 1.0) val maxHeightInPercentage: Float = 0.4f,
    val color: Int,
) {
    data class Builder(
        private var editText: EditText? = null,
        private var darkMode: Boolean = false,
        @FloatRange(from = 0.0, to = 1.0)
        private var maxHeightInPercentage: Float = 0.4f,
        private var color: Int = -1,
    ) {
        fun editText(editText: EditText?) = apply { this.editText = editText }

        fun darkMode(darkMode: Boolean) = apply {
            this.darkMode = darkMode
        }

        fun maxHeightInPercentage(
            @FloatRange(from = 0.0, to = 1.0)
            maxHeightInPercentage: Float,
        ) = apply {
            this.maxHeightInPercentage = maxHeightInPercentage
        }

        fun color(color: Int) = apply {
            this.color = color
        }

        fun build(): MemberTaggingExtras {
            if (editText == null) {
                throw Error("editText is a required attribute")
            }
            return MemberTaggingExtras(editText!!, darkMode, maxHeightInPercentage, color)
        }

    }

    fun toBuilder(): Builder {
        return Builder(
            editText,
            darkMode,
            maxHeightInPercentage,
            color
        )
    }
}