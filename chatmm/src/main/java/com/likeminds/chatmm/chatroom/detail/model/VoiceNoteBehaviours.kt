package com.likeminds.chatmm.chatroom.detail.model

import androidx.annotation.StringDef

//values for user behaviours
const val USER_CANCELING = "user canceling"
const val USER_LOCKING = "user locking"
const val USER_NONE = "user none"

//value for recording behaviours
const val RECORDING_CANCELLED = "recording cancelled"
const val RECORDING_LOCKED = "recording locked"
const val RECORDING_LOCK_DONE = "recording lock done"
const val RECORDING_RELEASED = "recording released"
const val RECORDING_SEND = "recording send"
const val RECORDING_LOCK_SEND = "recording lock send"

@StringDef(
    USER_CANCELING,
    USER_LOCKING,
    USER_NONE,
    RECORDING_CANCELLED,
    RECORDING_LOCKED,
    RECORDING_LOCK_DONE,
    RECORDING_RELEASED,
    RECORDING_SEND,
    RECORDING_LOCK_SEND
)

@Retention(AnnotationRetention.SOURCE)
internal annotation class VoiceNoteBehaviours