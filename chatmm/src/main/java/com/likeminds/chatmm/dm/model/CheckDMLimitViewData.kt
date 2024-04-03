package com.likeminds.chatmm.dm.model

class CheckDMLimitViewData private constructor(
    val isRequestDMLimitExceeded: Boolean?,
    val newRequestDMTimestamp: Long?,
    val numberInDuration: Int?,
    val duration: String?,
    val chatroomId: String?
) {
    class Builder {
        private var isRequestDMLimitExceeded: Boolean? = null
        private var newRequestDMTimestamp: Long? = null
        private var numberInDuration: Int? = null
        private var duration: String? = null
        private var chatroomId: String? = null

        fun isRequestDMLimitExceeded(isRequestDMLimitExceeded: Boolean?) =
            apply { this.isRequestDMLimitExceeded = isRequestDMLimitExceeded }

        fun newRequestDMTimestamp(newRequestDMTimestamp: Long?) =
            apply { this.newRequestDMTimestamp = newRequestDMTimestamp }

        fun numberInDuration(numberInDuration: Int?) =
            apply { this.numberInDuration = numberInDuration }

        fun duration(duration: String?) = apply { this.duration = duration }
        fun chatroomId(chatroomId: String?) = apply { this.chatroomId = chatroomId }

        fun build() = CheckDMLimitViewData(
            isRequestDMLimitExceeded,
            newRequestDMTimestamp,
            numberInDuration,
            duration,
            chatroomId
        )
    }

    fun toBuilder(): Builder {
        return Builder().isRequestDMLimitExceeded(isRequestDMLimitExceeded)
            .newRequestDMTimestamp(newRequestDMTimestamp)
            .numberInDuration(numberInDuration)
            .duration(duration)
            .chatroomId(chatroomId)
    }
}