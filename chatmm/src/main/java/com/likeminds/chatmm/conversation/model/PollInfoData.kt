package com.likeminds.chatmm.conversation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PollInfoData private constructor(
    val isAnonymous: Boolean?,
    val allowAddOption: Boolean?,
    val pollType: Int?,
    val pollTypeText: String?,
    val submitTypeText: String?,
    val expiryTime: Long?,
    val multipleSelectNum: Int?,
    val multipleSelectState: Int?,
    val pollViewDataList: List<PollViewData>?,
    val pollAnswerText: String?,
    val isPollSubmitted: Boolean?,
    val toShowResult: Boolean?
) : Parcelable {

    fun pollAnswerTextUpdated(): String {
        return if (this.pollAnswerText.isNullOrEmpty()) {
            "Be the first to respond"
        } else {
            pollAnswerText!!
        }
    }

    class Builder {
        private var isAnonymous: Boolean? = null
        private var allowAddOption: Boolean? = null
        private var pollType: Int? = null
        private var pollTypeText: String? = null
        private var submitTypeText: String? = null
        private var expiryTime: Long? = null
        private var multipleSelectNum: Int? = null
        private var multipleSelectState: Int? = null
        private var pollViewDataList: List<PollViewData>? = null
        private var pollAnswerText: String? = null
        private var isPollSubmitted: Boolean? = null
        private var toShowResult: Boolean? = null

        fun isAnonymous(isAnonymous: Boolean?) = apply { this.isAnonymous = isAnonymous }
        fun allowAddOption(allowAddOption: Boolean?) =
            apply { this.allowAddOption = allowAddOption }

        fun pollType(pollType: Int?) = apply { this.pollType = pollType }
        fun pollTypeText(pollTypeText: String?) = apply { this.pollTypeText = pollTypeText }
        fun submitTypeText(submitTypeText: String?) = apply { this.submitTypeText = submitTypeText }
        fun expiryTime(expiryTime: Long?) = apply { this.expiryTime = expiryTime }
        fun multipleSelectNum(multipleSelectNum: Int?) =
            apply { this.multipleSelectNum = multipleSelectNum }

        fun multipleSelectState(multipleSelectState: Int?) =
            apply { this.multipleSelectState = multipleSelectState }

        fun pollViewDataList(pollViewDataList: List<PollViewData>?) =
            apply { this.pollViewDataList = pollViewDataList }

        fun pollAnswerText(pollAnswerText: String?) = apply { this.pollAnswerText = pollAnswerText }
        fun isPollSubmitted(isPollSubmitted: Boolean?) =
            apply { this.isPollSubmitted = isPollSubmitted }

        fun toShowResult(toShowResult: Boolean?) = apply { this.toShowResult = toShowResult }

        fun build() = PollInfoData(
            isAnonymous,
            allowAddOption,
            pollType,
            pollTypeText,
            submitTypeText,
            expiryTime,
            multipleSelectNum,
            multipleSelectState,
            pollViewDataList,
            pollAnswerText,
            isPollSubmitted,
            toShowResult
        )
    }

    fun toBuilder(): Builder {
        return Builder().isAnonymous(isAnonymous)
            .allowAddOption(allowAddOption)
            .pollType(pollType)
            .pollTypeText(pollTypeText)
            .submitTypeText(submitTypeText)
            .expiryTime(expiryTime)
            .multipleSelectNum(multipleSelectNum)
            .multipleSelectState(multipleSelectState)
            .pollViewDataList(pollViewDataList)
            .pollAnswerText(pollAnswerText)
            .isPollSubmitted(isPollSubmitted)
            .toShowResult(toShowResult)
    }
}