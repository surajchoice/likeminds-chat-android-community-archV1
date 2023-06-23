package com.likeminds.chatmm.media.customviews.interfaces

fun interface OnProgressVideoListener {
    fun updateProgress(time: Float, max: Float, scale: Float)
}