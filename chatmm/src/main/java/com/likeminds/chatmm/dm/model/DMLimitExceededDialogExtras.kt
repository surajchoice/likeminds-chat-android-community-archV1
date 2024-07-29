package com.likeminds.chatmm.dm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DMLimitExceededDialogExtras private constructor(
    val newRequestDMTimestamp: Long?,
    val numberInDuration: Int?,
    val duration: String?,
) : Parcelable {
    class Builder {
        private var newRequestDMTimestamp: Long? = null
        private var numberInDuration: Int? = null
        private var duration: String? = null

        fun newRequestDMTimestamp(newRequestDMTimestamp: Long?) =
            apply { this.newRequestDMTimestamp = newRequestDMTimestamp }

        fun numberInDuration(numberInDuration: Int?) =
            apply { this.numberInDuration = numberInDuration }

        fun duration(duration: String?) = apply { this.duration = duration }

        fun build() = DMLimitExceededDialogExtras(
            newRequestDMTimestamp,
            numberInDuration,
            duration
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .newRequestDMTimestamp(newRequestDMTimestamp)
            .numberInDuration(numberInDuration)
            .duration(duration)
    }
}