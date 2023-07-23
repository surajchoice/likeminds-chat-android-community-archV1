package com.likeminds.chatmm.media.model

import androidx.annotation.StringDef

const val MEDIA_ACTION_PLAY = "play"
const val MEDIA_ACTION_PAUSE = "pause"
const val MEDIA_ACTION_NONE = "none"

@StringDef(
    MEDIA_ACTION_PLAY,
    MEDIA_ACTION_PAUSE,
    MEDIA_ACTION_NONE
)


@Retention(AnnotationRetention.SOURCE)
annotation class MediaActions
