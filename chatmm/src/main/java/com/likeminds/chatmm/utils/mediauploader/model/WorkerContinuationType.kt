package com.likeminds.chatmm.utils.mediauploader.model

import androidx.annotation.IntDef

const val WORKER_SUCCESS = 1
const val WORKER_RETRY = 2
const val WORKER_FAILURE = 3

@IntDef(
    WORKER_SUCCESS,
    WORKER_RETRY,
    WORKER_FAILURE
)

@Retention(AnnotationRetention.SOURCE)
annotation class WorkerContinuationType