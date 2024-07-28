package com.likeminds.chatmm.theme.customview.edittext

import android.net.Uri

internal interface LikeMindsEditTextListener {
    fun onMediaSelected(contentUri: Uri, mimeType: String)
}