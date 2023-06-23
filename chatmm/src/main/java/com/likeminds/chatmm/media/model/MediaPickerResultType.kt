package com.likeminds.chatmm.media.model

import androidx.annotation.IntDef

const val MEDIA_RESULT_PICKED = 100
const val MEDIA_RESULT_BROWSE = 101

@IntDef(
    MEDIA_RESULT_PICKED, MEDIA_RESULT_BROWSE
)

@Retention(AnnotationRetention.SOURCE)
internal annotation class MediaPickerResultType