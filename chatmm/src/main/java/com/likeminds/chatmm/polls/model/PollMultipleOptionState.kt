package com.likeminds.chatmm.polls.model

import androidx.annotation.IntDef

const val POLL_MULTIPLE_STATE_EXACTLY = 0
const val POLL_MULTIPLE_STATE_MAX = 1
const val POLL_MULTIPLE_STATE_LEAST = 2

@IntDef(
    POLL_MULTIPLE_STATE_EXACTLY,
    POLL_MULTIPLE_STATE_MAX,
    POLL_MULTIPLE_STATE_LEAST
)
@Retention(AnnotationRetention.SOURCE)
annotation class PollMultipleOptionState