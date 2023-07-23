package com.likeminds.chatmm.polls.model

import androidx.annotation.IntDef

const val POLL_TYPE_INSTANT = 0
const val POLL_TYPE_DEFERRED = 1

@IntDef(
    POLL_TYPE_INSTANT,
    POLL_TYPE_DEFERRED
)
@Retention(AnnotationRetention.SOURCE)
annotation class PollType