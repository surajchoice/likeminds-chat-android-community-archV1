package com.likeminds.chatmm.polls.model

import androidx.annotation.IntDef

const val POLL_SUCCESS = 1
const val POLL_FAILURE = 2
const val POLL_NO_UPDATE = 3

@IntDef(
    POLL_SUCCESS,
    POLL_FAILURE,
    POLL_NO_UPDATE
)
@Retention(AnnotationRetention.SOURCE)
internal annotation class PollResponse