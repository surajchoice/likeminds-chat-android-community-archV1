package com.likeminds.chatmm.branding.customview.edittext

import android.net.Uri

internal interface LikeMindsEditTextListener {
    fun onMediaSelected(contentUri: Uri, mimeType: String)
}