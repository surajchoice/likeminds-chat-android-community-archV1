package com.likeminds.chatmm.media.customviews.interfaces

import android.net.Uri
import com.likeminds.chatmm.media.model.VideoTrimExtras

interface OnTrimVideoListener {
    fun onVideoRangeChanged()
    fun onTrimStarted()
    fun getResult(uri: Uri, videoTrimExtras: VideoTrimExtras?)
    fun onFailed(videoTrimExtras: VideoTrimExtras?)
    fun cancelAction()
    fun onError(message: String)
}
