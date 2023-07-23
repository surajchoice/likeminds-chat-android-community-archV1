package com.likeminds.chatmm.chatroom.detail.util

interface VoiceNoteInterface {
    fun onVoiceNoteStarted() {}
    fun onVoiceNoteLocked() {}
    fun onVoiceNoteCompleted() {}
    fun onVoiceNoteCancelled() {}
    fun onVoiceNoteSend() {}

    fun isVoiceNoteLocked(): Boolean

    fun isDeletingVoiceNote(value: Boolean)
    fun stopTrackingVoiceNoteAction(value: Boolean)
}