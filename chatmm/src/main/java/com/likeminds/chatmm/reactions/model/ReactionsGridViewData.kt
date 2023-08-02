package com.likeminds.chatmm.reactions.model

class ReactionsGridViewData private constructor(
    val mostRecentReaction: String?,
    val secondMostRecentReaction: String?,
    val mostRecentReactionCount: Int?,
    val secondMostRecentReactionCount: Int?,
    val moreThanTwoReactionsPresent: Boolean?
) {
    class Builder {
        private var mostRecentReaction: String? = null
        private var secondMostRecentReaction: String? = null
        private var mostRecentReactionCount: Int? = null
        private var secondMostRecentReactionCount: Int? = null
        private var moreThanTwoReactionsPresent: Boolean? = null

        fun mostRecentReaction(mostRecentReaction: String?) =
            apply { this.mostRecentReaction = mostRecentReaction }

        fun secondMostRecentReaction(secondMostRecentReaction: String?) =
            apply { this.secondMostRecentReaction = secondMostRecentReaction }

        fun mostRecentReactionCount(mostRecentReactionCount: Int?) =
            apply { this.mostRecentReactionCount = mostRecentReactionCount }

        fun secondMostRecentReactionCount(secondMostRecentReactionCount: Int?) =
            apply { this.secondMostRecentReactionCount = secondMostRecentReactionCount }

        fun moreThanTwoReactionsPresent(moreThanTwoReactionsPresent: Boolean?) =
            apply { this.moreThanTwoReactionsPresent = moreThanTwoReactionsPresent }

        fun build() = ReactionsGridViewData(
            mostRecentReaction,
            secondMostRecentReaction,
            mostRecentReactionCount,
            secondMostRecentReactionCount,
            moreThanTwoReactionsPresent
        )
    }

    fun toBuilder(): Builder {
        return ReactionsGridViewData.Builder()
            .mostRecentReaction(mostRecentReaction)
            .secondMostRecentReaction(secondMostRecentReaction)
            .mostRecentReactionCount(mostRecentReactionCount)
            .secondMostRecentReactionCount(secondMostRecentReactionCount)
            .moreThanTwoReactionsPresent(moreThanTwoReactionsPresent)
    }
}